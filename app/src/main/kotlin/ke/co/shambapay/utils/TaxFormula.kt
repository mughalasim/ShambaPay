package ke.co.shambapay.utils

class TaxFormula {

    fun getPayForGrossIncome(gross: Double): Double {
        var value1 = 0.0
        var value2 = 0.0
        var value3 = 0.0

        if (gross >= 24000.0){
            value1 = (gross - 0.0) * 0.1
        }

        if (gross >= 32333.0){
            value2 = (gross - 24001.0) * 0.15
        }

        if (gross > 32334){
            value3 = (gross - 32333.0) * 0.05
        }

        return value1 + value2 + value3

    }

    fun getNssfRate():Double {
        return 200.0
    }

    fun getPersonalRelief(taxableIncome : Double):Double {
        return when(taxableIncome){
            in 0.0 .. 24001.0 -> {1408.0}
            else -> {2400.0}
        }
    }

    fun getNhifForGrossIncome(gross: Double): Double {
        return when (gross) {
            in 0.0..5999.0 -> {
                150.0
            }
            in 6000.0..7999.0 -> {
                300.0
            }
            in 8000.0..11999.0 -> {
                400.0
            }
            in 12000.0..14999.0 -> {
                500.0
            }
            in 15000.0..19999.0 -> {
                600.0
            }
            in 20000.0..24999.0 -> {
                750.0
            }
            in 25000.0..29999.0 -> {
                850.0
            }
            in 30000.0..34999.0 -> {
                900.0
            }
            in 35000.0..39999.0 -> {
                950.0
            }
            in 40000.0..44999.0 -> {
                1000.0
            }
            in 45000.0..49999.0 -> {
                1100.0
            }
            in 50000.0..59999.0 -> {
                1200.0
            }
            in 60000.0..69999.0 -> {
                1300.0
            }
            in 70000.0..79999.0 -> {
                1400.0
            }
            in 80000.0..89999.0 -> {
                1500.0
            }
            in 90000.0..99999.0 -> {
                1600.0
            }
            else -> {
                1700.0
            }
        }
    }
}