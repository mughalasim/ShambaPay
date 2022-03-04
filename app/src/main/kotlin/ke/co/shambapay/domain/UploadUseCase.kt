package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.utils.CSVReader
import ke.co.shambapay.utils.toMonthYearString
import kotlinx.coroutines.CompletableDeferred
import org.joda.time.DateTime
import java.io.InputStream

class UploadUseCase: BaseUseCase<UploadUseCase.Input, Unit, Failures>() {

    sealed class Input {
        data class Employees(val inputStream : InputStream?, val companyId: String): Input()
        data class Work(val inputStream : InputStream?, val month : Int, val year: Int ,val companyId: String): Input()
    }

    override suspend fun run(input: Input): BaseResult<Unit, Failures> {

        when(input){
            is Input.Employees ->{

                val result = parseEmployeeInputStream(input.inputStream)

                if (result is BaseResult.Failure) return result

                val employeeList: List<EmployeeEntity> = (result as BaseResult.Success).successType

                for (employee in employeeList){
                    val uploadResult = uploadSingleEmployee(employee, input.companyId)
                    if (uploadResult is BaseResult.Failure) return uploadResult
                }

                return BaseResult.Success(Unit)

            }

            is Input.Work ->{

                val result = parseWorkInputStream(input.inputStream, input.month, input.year, input.companyId)

                if (result is BaseResult.Failure) return result

                return BaseResult.Success(Unit)

            }
        }

    }

    private suspend fun uploadSingleEmployee(employeeEntity: EmployeeEntity, companyId: String): BaseResult<Unit, Failures> {
        val def = CompletableDeferred<BaseResult<Unit, Failures>>()
        FirebaseDatabase.getInstance().getReference(
            QueryBuilder.getEmployees(companyId) + "/" + employeeEntity.id).setValue(employeeEntity).
        addOnSuccessListener{
            def.complete(BaseResult.Success(Unit))

        }.addOnFailureListener {
            def.complete(BaseResult.Failure(Failures.WithMessage(it.localizedMessage)))
        }
        return def.await()
    }

    private fun parseEmployeeInputStream(inputStream: InputStream?): BaseResult<List<EmployeeEntity>, Failures> {
        if (inputStream == null){
            return  BaseResult.Failure(Failures.WithMessage("Please select a file"))
        }

        val reader: CSVReader?
        try {
            reader = CSVReader(inputStream.bufferedReader())
        } catch (e: Exception){
            return BaseResult.Failure(Failures.WithMessage("Invalid data in the file"))
        }

        var row: List<String>?
        row = reader.readRow()?.asList()

        if (row.isNullOrEmpty()) {
            return BaseResult.Failure(Failures.WithMessage("Missing data in the file"))
        }

        row = reader.readRow()?.asList()

        val mutableList = mutableListOf<EmployeeEntity>()

        while(row?.isNotEmpty() == true) {

            val columns = row[0].split(",")

            if (columns[0].isEmpty()) {
                return BaseResult.Failure(Failures.WithMessage("Missing Employee ID on one of the employees"))
            }

            if (columns[1].isEmpty()) {
                return BaseResult.Failure(Failures.WithMessage("Missing Employee First Name on one of the employees"))
            }

            if (columns[4].isEmpty()) {
                return BaseResult.Failure(Failures.WithMessage("Missing Employee Phone number on one of the employees"))
            }

            try {
                mutableList.add (
                    EmployeeEntity (
                        id = columns[0].toIntOrNull(),
                        nationalId = columns[3].toLongOrNull(),
                        firstName = columns[1],
                        lastName = columns[2],
                        cash = 0.0,
                        advance = 0.0,
                        misc = 0.0,
                        nhif = columns[6],
                        nssf = columns[5],
                        phone = columns[4].toLong(),
                        areaCode = 245
                    )
                )
            } catch (e: Exception){
                return BaseResult.Failure(Failures.WithMessage(e.localizedMessage + "On row: " + columns))
            }

            row = reader.readRow()?.asList()

        }

        return BaseResult.Success(mutableList)

    }

    private fun parseWorkInputStream(inputStream: InputStream?, month : Int, year: Int, companyId: String): BaseResult<Unit, Failures> {
        if (inputStream == null){
            return  BaseResult.Failure(Failures.WithMessage("Please select a file"))
        }

        val reader: CSVReader?
        try {
            reader = CSVReader(inputStream.bufferedReader())
        } catch (e: Exception){
            return BaseResult.Failure(Failures.WithMessage("Invalid data in the file"))
        }

        var row: List<String>?
        row = reader.readRow()?.asList()

        if (row.isNullOrEmpty()) {
            return BaseResult.Failure(Failures.WithMessage("Missing data in the file"))
        }

        row = reader.readRow()?.asList()

        while(row?.isNotEmpty() == true) {

            val columns = row[0].split(",")

            if (columns[0].isEmpty()) {
                return BaseResult.Failure(Failures.WithMessage("Missing Employee ID on one of the employees"))
            }

            if (columns[1].isEmpty()) {
                return BaseResult.Failure(Failures.WithMessage("Missing Job ID on one of the employees"))
            }

            if (columns[2].isEmpty()) {
                return BaseResult.Failure(Failures.WithMessage("Missing Rate on one of the employees"))
            }

            val employeeId = columns[0]
            val rateId = columns[1].trim() + columns[2].trim()

            val workList = columns.drop(3)

            workList.mapIndexed { index, work ->
                try {
                    val date = DateTime.now().withDate(year, month, index+1)
                    FirebaseDatabase.getInstance().reference
                        .child(QueryBuilder.getWork(companyId, employeeId) +"/"+ date.toMonthYearString() + "/${index+1}")
                        .setValue(WorkEntity (
                            date = date.toString(),
                            unit = work.toDoubleOrNull(),
                            rateId = rateId
                        ))

                } catch (e: Exception){
                    return BaseResult.Failure(Failures.WithMessage(e.localizedMessage))
                }
            }

            row = reader.readRow()?.asList()

        }

        return BaseResult.Success(Unit)

    }

}