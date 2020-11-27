package io.github.jasonheo

import java.time.LocalDate

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

object Main {
  def main(args: Array[String]): Unit = {
    basicSerialize()
    prettyPrint()

    localDateDefaultFormat()
    localDateCustomFormat1()
    localDateCustomFormat2()
    localDateCustomFormat3()

    basicDeserialize1()
    basicDeserialize2()
    basicDeserialize3()
  }

  def basicSerialize(): Unit = {
    case class Person(
                       name: String,
                       hasCat: Boolean
                     )

    val you: Person = Person(
      name = "Kim",
      hasCat = true
    )

    val objectMapper = new ObjectMapper() with ScalaObjectMapper

    objectMapper.registerModule(DefaultScalaModule)

    println(objectMapper.writeValueAsString(you))

    // 출력 결과
    // {"name":"Kim","hasCat":true}
  }

  // 출처: https://mkyong.com/java/how-to-enable-pretty-print-json-output-jackson/
  def prettyPrint(): Unit = {
    case class Person(
                       name: String,
                       hasCat: Boolean
                     )

    val you: Person = Person(
      name = "Kim",
      hasCat = true
    )

    val objectMapper = new ObjectMapper() with ScalaObjectMapper

    objectMapper.registerModule(DefaultScalaModule)

    println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(you))

    // 출력 결과
    // {
    //   "name" : "Kim",
    //   "hasCat" : true
    //  }
  }

  case class LocalDateFormat(
                              name: String,
                              hasCat: Boolean,

                              birthDate: LocalDate
                            )

  def localDateDefaultFormat(): Unit = {
    val you = LocalDateFormat(
      name = "Kim",
      hasCat = true,
      birthDate = LocalDate.of(2000, 11, 23)
    )

    val objectMapper = new ObjectMapper() with ScalaObjectMapper

    objectMapper.registerModule(DefaultScalaModule)

    println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(you))

    // 출력 결과
    // {
    //   "name" : "Kim",
    //   "hasCat" : true,
    //   "birthDate" : {
    //     "year" : 2000,
    //     "month" : "NOVEMBER",
    //     "chronology" : {
    //     "calendarType" : "iso8601",
    //     "id" : "ISO"
    //   },
    //     "monthValue" : 11,
    //     "dayOfMonth" : 23,
    //     "era" : "CE",
    //     "dayOfYear" : 328,
    //     "dayOfWeek" : "THURSDAY",
    //     "leapYear" : true
    //   }
    // }
  }

  case class LocalDateTimeCustomFormat1(
                                         name: String,
                                         hasCat: Boolean,

                                         birthDate: LocalDate
                                       )

  // 출처1: https://www.baeldung.com/jackson-serialize-dates#iso-8601
  // 출처2: https://perfectacle.github.io/2018/01/16/jackson-local-date-time-serialize/
  def localDateCustomFormat1(): Unit = {
    val you = LocalDateTimeCustomFormat1(
      name = "Kim",
      hasCat = true,
      birthDate = LocalDate.of(2000, 11, 23)
    )

    val objectMapper = new ObjectMapper() with ScalaObjectMapper

    objectMapper.registerModule(DefaultScalaModule)
    objectMapper.registerModule(new JavaTimeModule)

    println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(you))
  }

  case class LocalDateCustomFormat2(
                                     name: String,
                                     hasCat: Boolean,

                                     birthDate: LocalDate
                                   )

  // 출처: 상동
  def localDateCustomFormat2(): Unit = {

    val you = LocalDateCustomFormat2(
      name = "Kim",
      hasCat = true,
      birthDate = LocalDate.of(2000, 11, 23)
    )

    val objectMapper = new ObjectMapper() with ScalaObjectMapper

    objectMapper.registerModule(DefaultScalaModule)
    objectMapper.registerModule(new JavaTimeModule)

    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(you))

    // 출력 결과
    // {
    //   "name" : "Kim",
    //   "hasCat" : true,
    //   "birthDate" : [ 2000, 11, 23 ]
    // }
  }

  // @JsonFormat test
  case class LocalDateCustomFormat3(
                                     name: String,
                                     hasCat: Boolean,

                                     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
                                     birthDate: LocalDate
                                   )

  def localDateCustomFormat3(): Unit = {

    val you = LocalDateCustomFormat3(
      name = "Kim",
      hasCat = true,
      birthDate = LocalDate.of(2000, 11, 23)
    )

    val objectMapper = new ObjectMapper() with ScalaObjectMapper

    objectMapper.registerModule(DefaultScalaModule)
    objectMapper.registerModule(new JavaTimeModule())

    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(you))

    // 출력 결과
    // {
    //   "name" : "Kim",
    //   "hasCat" : true,
    //   "birthDate" : "2000-11-23"
    // }
  }

  case class PersonDeserialize1(
                                 name: String,
                                 hasCat: Boolean
                               )


  // 출처: https://stackoverflow.com/a/31867613/2930152
  def basicDeserialize1(): Unit = {

    val jsonStr = """
    {
      "name": "Kim",
      "hasCat": false
    }
    """

    val objectMapper = new ObjectMapper() with ScalaObjectMapper

    objectMapper.registerModule(DefaultScalaModule)

    println(objectMapper.readValue[PersonDeserialize1](jsonStr))

    // 출력 결과
    // PersonDeserialize1(Kim,false)
  }

  case class PersonDeserialize2(
                                 name: String,
                                 hasCat: Boolean,
                                 gender: String,
                                 age: Long
                               )

  // configure test
  //   - json string에 필드가 누락된 경우 에러 테스트
  //   - json의 int를 Long Type으로 변환
  def basicDeserialize2(): Unit = {
    val jsonStr = """
    {
      "name": "Kim",
      "hasCat": false,
      "age": 1
    }
    """

    val objectMapper = new ObjectMapper()

      // 필드가 없는 경우 에러 테스트 -> 에러가 발생하지 않네;;
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

      // true든 false든 deserialize가 잘 된다;;
      .configure(DeserializationFeature.USE_LONG_FOR_INTS, true)
      .registerModule(DefaultScalaModule)

    println(objectMapper.readValue(jsonStr, classOf[PersonDeserialize2]))

    // 출력 결과
    // PersonDeserialize2(Kim,false,null,1)
  }

  // Map으로 변환
  def basicDeserialize3(): Unit = {
    val jsonStr = """
    {
      "name": "Kim",
      "hasCat": false,
      "age": 1
    }
    """

    val objectMapper = new ObjectMapper()

    objectMapper.registerModule(DefaultScalaModule)

    println(objectMapper.readValue(jsonStr, classOf[Map[String, Any]]))

    // 출력 결과
    // Map(name -> Kim, hasCat -> false, age -> 1)
  }
}
