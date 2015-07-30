package com.ecfront.common

import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * Simple Request VO
 * @param cId  Current Request ID
 * @param accountId Request Account ID
 */
case class SReq(override val cId: String, override val accountId: String) extends Req(cId, accountId, null, null)


/**
 * Request VO
 * @param cId  Current Request ID
 * @param accountId Request Account ID
 */
class Req(val cId: String, val accountId: String, val method: String, val uri: String)

object Req {
  val CID = "cid"
  val ACCOUNT_ID = "accountId"
  val METHOD = "method"
  val URI = "uri"

  def apply(cId: String, accountId: String, method: String, uri: String) = new Req(cId, accountId, method, uri)

}

/**
 * Response VO
 * @param code Standard Code
 * @param message  Description
 * @param _body Response main info
 */
case class Resp[M](code: String, message: String, private val _body: Option[M]) {
  var body: M = _
}

object Resp extends LazyLogging {

  val CODE = "code"
  val BODY = "body"
  val MESSAGE = "message"
  val CUSTOM_CODE_PREFIX = "custom-"

  def success[M](body: M) = {
    val res = Resp[M](StandardCode.SUCCESS, "", Some(body))
    res.body = body
    res
  }

  def notFound[E](message: String) = {
    logger.warn("[Result] [%s] Not found: %s".format(StandardCode.NOT_FOUND, message))
    Resp[E](StandardCode.NOT_FOUND, message, null)
  }

  def badRequest[E](message: String) = {
    logger.warn("[Result] [%s] Bad request: %s".format(StandardCode.BAD_REQUEST, message))
    Resp[E](StandardCode.BAD_REQUEST, message, null)
  }

  def forbidden[E](message: String) = {
    logger.warn("[Result] [%s] Forbidden: %s".format(StandardCode.FORBIDDEN, message))
    Resp[E](StandardCode.FORBIDDEN, message, null)
  }

  def unAuthorized[E](message: String) = {
    logger.warn("[Result] [%s] Unauthorized: %s".format(StandardCode.UNAUTHORIZED, message))
    Resp[E](StandardCode.UNAUTHORIZED, message, null)
  }

  def serverError[E](message: String) = {
    logger.error("[Result] [%s] Server error: %s".format(StandardCode.INTERNAL_SERVER_ERROR, message))
    Resp[E](StandardCode.INTERNAL_SERVER_ERROR, message, null)
  }

  def notImplemented[E](message: String) = {
    logger.error("[Result] [%s] Not implemented: %s".format(StandardCode.NOT_IMPLEMENTED, message))
    Resp[E](StandardCode.NOT_IMPLEMENTED, message, null)
  }

  def serverUnavailable[E](message: String) = {
    logger.error("[Result] [%s] Server unavailable: %s".format(StandardCode.SERVICE_UNAVAILABLE, message))
    Resp[E](StandardCode.SERVICE_UNAVAILABLE, message, null)
  }

  def unknown[M](message: String) = {
    logger.error("[Result] [%s] unknown fail: %s".format(StandardCode.UNKNOWN, message))
    Resp[M](StandardCode.UNKNOWN, message, null)
  }

  def customFail[M](code: String, message: String) = {
    logger.error("[Result] [%s] Custom fail: %s".format(CUSTOM_CODE_PREFIX + code, message))
    Resp[M](CUSTOM_CODE_PREFIX + code, message, null)
  }

  implicit def isSuccess[M](dto: Resp[M]): Boolean = StandardCode.SUCCESS == dto.code

  implicit def convertFail[M](dto: Resp[_]): Resp[M] = Resp[M](dto.code, dto.message, null)

}

/**
 * Standard Code
 */
object StandardCode extends Enumeration {
  val SUCCESS = Value("200").toString
  val BAD_REQUEST = Value("400").toString
  val UNAUTHORIZED = Value("401").toString
  val FORBIDDEN = Value("403").toString
  val NOT_FOUND = Value("404").toString
  val INTERNAL_SERVER_ERROR = Value("500").toString
  val NOT_IMPLEMENTED = Value("501").toString
  val SERVICE_UNAVAILABLE = Value("503").toString
  val UNKNOWN = Value("-1").toString
}
