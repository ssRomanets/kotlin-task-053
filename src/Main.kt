import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis


suspend fun main() {
    var idMList:        MutableList<String> = mutableListOf()
    var passwordMList:  MutableList<String> = mutableListOf()

    println("Ввести количество паролeй: ")
    var length = readLine().toString().toInt()

    println("Ввести символ с которого начинается каждый пароль: ")
    var input = readLine().toString()

    var time = measureTimeMillis {
        withContext(newSingleThreadContext("product_thread_context")) {
            launch {
                idMList = getListId(length)
            }

            launch {
                passwordMList = getListOfPassword(input, length)
            }
        }
    }

    // ("Минимальное время на создание 100000 паролей при задержке в
    // 1 миллисекунду по каждому паролю составляет 1579200 миллисекунд.")
    println("Время на создание ${length} паролей равно ${time}. ")

    println("Выводим результирующую карту.")
    val resultMap: Map<String, String> = idMList.zip(passwordMList).toMap()
    println(resultMap.toString())
}

suspend fun getListId(length: Int): MutableList<String>
{
    var curList:        MutableList<String> = mutableListOf()
    getIdFlow(length).collect{ id ->
        curList.addLast(id)
    }
    return  curList;
}

suspend fun getListOfPassword(input: String, length: Int): MutableList<String>
{
    var curList:        MutableList<String> = mutableListOf()
    getPasswordFlow(input, length).collect{ password ->
        curList.addLast(password)
    }
    return  curList;
}

fun getIdFlow(length: Int) = flow {
    for (i in 0..length-1) {
        delay(1L)
        if (i % 100 == 0) println("getIdFlow $i")

        var idString = i.toString();
        idString = (idString).padStart(6, '0')

        emit(idString)
    }
}

fun getPasswordFlow(input: String, length: Int) = flow {
    val charset = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    for (i in 0..length-1) {
        delay(1L)
        if (i % 100 == 0) println("getPasswordFlow $i")

        var curPassword = input + (0..4).map { charset.random() }.joinToString("")
        for (j in curPassword.indices)  if (curPassword[j].isLetter() && j % 2 == 1) curPassword[j].toUpperCase()

        emit (curPassword)
    }
}