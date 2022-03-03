package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.utils.CSVReader
import ke.co.shambapay.utils.toStringFromDayMonthAndYear
import kotlinx.coroutines.CompletableDeferred
import org.joda.time.DateTime
import java.io.InputStream

class UploadUseCase: BaseUseCase<UploadUseCase.Input, Boolean, Failures>() {

    sealed class Input {
        data class Employees(val inputStream : InputStream?, val companyId: String): Input()
        data class Work(val inputStream : InputStream?, val month : Int, val year: Int ,val companyId: String): Input()
    }

    override suspend fun run(input: Input): BaseResult<Boolean, Failures> {

        when(input){
            is Input.Employees ->{

                val result = parseEmployeeInputStream(input.inputStream)

                if (result is Failures) return BaseResult.Failure(result)

                for (employee in (result as BaseResult.Success).successType){
                    val uploadResult = uploadSingleEmployee(employee, input.companyId)
                    if (uploadResult is Failures) return uploadResult
                }

                return BaseResult.Success(true)

            }

            is Input.Work ->{
                return BaseResult.Success(true)

            }
        }

    }

    private suspend fun uploadSingleEmployee(employeeEntity: EmployeeEntity, companyId: String): BaseResult<Boolean, Failures> {
        val def = CompletableDeferred<BaseResult<Boolean, Failures>>()
        FirebaseDatabase.getInstance().getReference(QueryBuilder.getEmployees(companyId) + "/" +
                employeeEntity.id).setValue(employeeEntity).

        addOnSuccessListener{
            BaseResult.Success(true)

        }.addOnFailureListener {
            BaseResult.Failure(Failures.WithMessage(it.localizedMessage))
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
                        id = columns[0].toInt(),
                        nationalId = columns[3].toInt(),
                        firstName = columns[1],
                        lastName = columns[2],
                        cash = 0.0,
                        advance = 0.0,
                        misc = 0.0,
                        nhif = columns[6],
                        nssf = columns[5],
                        phone = columns[4].toInt(),
                        areaCode = 245
                    )
                )
            } catch (e: Exception){
                return BaseResult.Failure(Failures.WithMessage(e.localizedMessage))
            }

            row = reader.readRow()?.asList()

        }

        return BaseResult.Success(mutableList)

    }

    private fun parseWorkInputStream(inputStream: InputStream?, month : Int, year: Int, companyId: String): BaseResult<List<WorkEntity>, Failures> {
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

        val mutableList = mutableListOf<WorkEntity>()

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
            val rateId = columns[1]+columns[2]

            val workList = columns.drop(3)

            workList.mapIndexed { index, work ->
                try {
                    mutableList.add (
                        WorkEntity (
                            date = DateTime.now().toStringFromDayMonthAndYear(index ,month, year),
                            unit = work.toDouble(),
                            rateId = rateId
                        )
                    )
                } catch (e: Exception){
                    return BaseResult.Failure(Failures.WithMessage(e.localizedMessage))
                }
            }

            FirebaseDatabase.getInstance().reference
                .child(QueryBuilder.getWork(employeeId, companyId) + "/")
                .child("Employees")
                .child(split[0])
                .child("work").child(input.year).child(input.month).child("${i+1}")
                .setValue(WorkEntity("${input.year}.${input.month}.${i+1}", unit, JobType.Picking))


            row = reader.readRow()?.asList()

        }

        return BaseResult.Success(mutableList)

    }

}