package ke.co.shambapay.domain

import com.google.firebase.database.FirebaseDatabase
import ke.co.shambapay.data.model.EmployeeEntity
import ke.co.shambapay.data.model.JobType
import ke.co.shambapay.data.model.WorkEntity
import ke.co.shambapay.domain.base.BaseResult
import ke.co.shambapay.domain.base.BaseUseCase
import ke.co.shambapay.utils.CSVReader
import org.joda.time.DateTime
import java.io.InputStream

class PostCSVUseCase: BaseUseCase<PostCSVUseCase.Input, PostCSVUseCase.Output, Failures>() {

    sealed class Input {
        data class Csv(val year: String, val month: String, val inputStream : InputStream?): Input()
    }

    sealed class Output {
        object Empty: Output()
    }

    override suspend fun run(input: Input): BaseResult<Output, Failures> {

        try {
            when(input){

                is Input.Csv -> {

                    if (input.year.isEmpty())  {
                        return BaseResult.Failure(Failures.WithMessage("Please set the year"))
                    }
                    if (input.month.isEmpty()) {
                        return BaseResult.Failure(Failures.WithMessage("Please set the month"))
                    }

                    if(input.inputStream == null){
                        return  BaseResult.Failure(Failures.WithMessage("Please select a file"))
                    }

                    val reader: CSVReader?
                    try {
                        reader = CSVReader(input.inputStream.bufferedReader())
                    } catch (e: Exception){
                        return BaseResult.Failure(Failures.WithMessage("Invalid data in file"))
                    }


                    var column: List<String>?
                    column = reader.readRow()?.asList()

                    if (column.isNullOrEmpty()) {
                        return BaseResult.Failure(Failures.WithMessage("Invalid data in file"))
                    }

                    while(column?.isNotEmpty() == true) {

                        val split = column[0].split(",")

                        val works = split.drop(1)

                        println("Name: " +  split[0] )

                        if (works.isEmpty()) {
                            return BaseResult.Failure(Failures.WithMessage("No work added"))
                        }

                        if (works.size > 31) {
                            return BaseResult.Failure(Failures.WithMessage("Invalid data in file"))
                        }

                        FirebaseDatabase.getInstance().reference
                            .child("FARM_CSV_SAMPLE")
                            .child("Employees")
                            .child(split[0])
                            .setValue(EmployeeEntity (
                                firstName = split[0]
                            ))

                        works.mapIndexed { i, work ->
                            val unit = work.toDoubleOrNull() ?: 0.0
                            println("Date worked: " +  "${input.year}.${input.month}.${i+1}" + " Unit: " + unit)

                            FirebaseDatabase.getInstance().reference
                                .child("FARM_CSV_SAMPLE")
                                .child("Work")
                                .child(split[0])
                                .child(input.year)
                                .child(input.month)
                                .child("${i+1}")
                                .setValue(WorkEntity(DateTime.now().withDate(i, input.month.toInt(), input.year.toInt()), unit, JobType.Picking))
                        }

                        column = reader.readRow()?.asList()

                    }

                     /*

                    FARM NAME
                       1. Employees
                       2. Work
                       3. Users
                       4. Settings - JobRates, Name, Tel
                    */

                    return BaseResult.Success(Output.Empty)

                }

            }
        } catch (e: Exception) {
            return BaseResult.Failure(Failures.WithMessage(e.message ?: "Exception thrown"))
        }
    }
}