package com.capol.notify.manage.domain;


import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

public enum EnumExceptionCode {
    // Global
    OK(HttpStatus.OK, "200"),
    BadRequest(HttpStatus.BAD_REQUEST, "BadRequest"),
    IllegalState(HttpStatus.BAD_REQUEST, "IllegalState"),
    Unauthorized(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    Forbidden(HttpStatus.FORBIDDEN, "Forbidden"),
    NotFound(HttpStatus.NOT_FOUND, "NotFound"),
    InternalServerError(HttpStatus.INTERNAL_SERVER_ERROR, "InternalServerError"),
    NotImplemented(HttpStatus.NOT_IMPLEMENTED, "NotImplemented"),

    // User
    FailedToSendAuthCode(HttpStatus.BAD_REQUEST, "FailedToSendAuthCode"),
    IllegalAuthCode(HttpStatus.BAD_REQUEST, "IllegalAuthCode"),
    IllegalAccountOrPassword(HttpStatus.BAD_REQUEST, "IllegalAccountOrPassword"),

    // Role-Permission
    RoleNotExists(HttpStatus.BAD_REQUEST, "RoleNotExists"),
    RoleNameDuplicate(HttpStatus.BAD_REQUEST, "RoleNameDuplicate"),

    // Role-User
    UserNotExists(HttpStatus.BAD_REQUEST, "UserNotExists"),
    UserAccountAlreadyExists(HttpStatus.BAD_REQUEST, "UserAccountAlreadyExists"),
    UserPhoneAlreadyExists(HttpStatus.BAD_REQUEST, "UserPhoneAlreadyExists"),
    UserEmailAlreadyExists(HttpStatus.BAD_REQUEST, "UserEmailAlreadyExists"),
    UserCanNotDisableSelf(HttpStatus.BAD_REQUEST, "UserCanNotDisableSelf"),
    UserCanNotDeleteSelf(HttpStatus.BAD_REQUEST, "UserCanNotDeleteSelf"),
    UserAlreadyInRole(HttpStatus.BAD_REQUEST, "UserAlreadyInRole"),
    UserNotInRole(HttpStatus.BAD_REQUEST, "UserNotInRole"),

    // Fund
    FundNotExists(HttpStatus.BAD_REQUEST, "FundNotExists"),
    FundCompanyNotExists(HttpStatus.BAD_REQUEST, "FundCompanyNotExists"),
    FundIsinNotExists(HttpStatus.BAD_REQUEST, "FundIsinNotExists"),
    FundIsinExists(HttpStatus.BAD_REQUEST, "FundIsinExists"),
    FundProductCodeNotExists(HttpStatus.BAD_REQUEST, "FundProductCodeNotExists"),
    FundProductCodeExists(HttpStatus.BAD_REQUEST, "FundProductCodeExists"),
    FundCloseDateNotExists(HttpStatus.BAD_REQUEST, "FundCloseDateNotExists"),
    FundCloseDateAndFundIsinExists(HttpStatus.BAD_REQUEST, "FundCloseDateAndFundIsinExists"),
    FundReasonNotExists(HttpStatus.BAD_REQUEST, "FundReasonNotExists"),
    FundCloseDayNotExists(HttpStatus.BAD_REQUEST, "FundCloseDayNotExists"),
    MarketCloseDayExists(HttpStatus.BAD_REQUEST, "MarketCloseDayExists"),
    MarketCloseDayNoExists(HttpStatus.BAD_REQUEST, "MarketCloseDayNoExists"),
    FundTradeRuleNotExists(HttpStatus.BAD_REQUEST, "FundTradeRuleNotExists"),
    FundTradeRuleAlreadyExists(HttpStatus.OK, "FundTradeRuleAlreadyExists"),
    InvalidCurrencyType(HttpStatus.BAD_REQUEST, "InvalidCurrencyType"),

    // Fund - Sync
    FundIsSyncing(HttpStatus.BAD_REQUEST, "FundIsSyncing"),
    FundNameNotExists(HttpStatus.BAD_REQUEST, "FundNameNotExists"),
    FundManagerNotExists(HttpStatus.BAD_REQUEST, "FundManagerNotExists"),
    FundManagerIdNotExists(HttpStatus.BAD_REQUEST, "FundManagerIdNotExists"),
    FundMarketTypeNotExists(HttpStatus.BAD_REQUEST, "FundMarketTypeNotExists"),

    // Trading - Customer
    TradingRuleRestricted(HttpStatus.BAD_REQUEST, "TradingRuleRestricted"),
    FundCanNotBuy(HttpStatus.BAD_REQUEST, "FundCanNotBuy"),
    FundCanNotSell(HttpStatus.BAD_REQUEST, "FundCanNotSell"),
    CustomerRegionNotSupport(HttpStatus.BAD_REQUEST, "CustomerRegionNotSupport"),
    ProfessionalInvestorOnly(HttpStatus.BAD_REQUEST, "ProfessionalInvestorOnly"),
    RiskEvaluationNotSatisfied(HttpStatus.BAD_REQUEST, "RiskEvaluationNotSatisfied"),
    DerivativesKnowledgeNotSatisfied(HttpStatus.BAD_REQUEST, "DerivativesKnowledgeNotSatisfied"),
    RiskEvaluationAndDerivativesKnowledgeNotSatisfied(HttpStatus.BAD_REQUEST, "RiskEvaluationAndDerivativesKnowledgeNotSatisfied"),
    InsufficientBalance(HttpStatus.BAD_REQUEST, "InsufficientBalance"),
    InsufficientPosition(HttpStatus.BAD_REQUEST, "InsufficientPosition"),
    CustomerTradeOrderNotExists(HttpStatus.BAD_REQUEST, "CustomerTradeOrderNotExists"),
    CustomerOrderNotExists(HttpStatus.BAD_REQUEST, "CustomerOrderNotExists"),
    CustomerNotFound(HttpStatus.BAD_REQUEST, "CustomerNotFound"),
    InvalidTradeAccount(HttpStatus.BAD_REQUEST, "InvalidTradeAccount"),
    MinFirstInvestmentAmountNotSatisfied(HttpStatus.BAD_REQUEST, "MinFirstInvestmentAmountNotSatisfied"),
    MinNextInvestmentAmountNotSatisfied(HttpStatus.BAD_REQUEST, "MinNextInvestmentAmountNotSatisfied"),
    MinInvestmentAmountNotFound(HttpStatus.BAD_REQUEST, "MinInvestmentAmountNotFound"),
    MinRansomAmountNotSatisfied(HttpStatus.BAD_REQUEST, "MinRansomAmountNotSatisfied"),
    MinHoldAmountAmountNotSatisfied(HttpStatus.BAD_REQUEST, "MinHoldAmountAmountNotSatisfied"),
    InvalidFeeType(HttpStatus.BAD_REQUEST, "InvalidFeeType"),


    // Trading - Dividend
    BrokerDividendOrderNotExists(HttpStatus.BAD_REQUEST, "BrokerDividendOrderNotExists"),
    FundDividendNotExists(HttpStatus.BAD_REQUEST, "FundDividendNotExists"),
    FundDividendExDateInvalid(HttpStatus.BAD_REQUEST, "FundDividendExDateInvalid"),

    // Trading - User
    CollectiveOrderNotExists(HttpStatus.BAD_REQUEST, "CollectiveOrderNotExists"),
    DuplicatedBrokerOrder(HttpStatus.BAD_REQUEST, "DuplicatedBrokerOrder"),
    InvalidOrderType(HttpStatus.BAD_REQUEST, "InvalidOrderType"),
    NetAssetValueUnavailable(HttpStatus.INTERNAL_SERVER_ERROR, "NetAssetValueUnavailable"),

    // Trading - ae
    AeUnitCodeNotExists(HttpStatus.BAD_REQUEST, "AeUnitCodeNotExists"),
    CustomerNoAeUnit(HttpStatus.BAD_REQUEST, "CustomerNoAeUnit"),

    // Privately Offered Fund
    PrivatelyOfferedFundAlreadyExists(HttpStatus.BAD_REQUEST, "PrivatelyOfferedFundAlreadyExists"),
    PrivatelyOfferedFundNotExists(HttpStatus.BAD_REQUEST, "PrivatelyOfferedFundNotExists"),

    PrivatelyOfferedFundAgencyAlreadyExists(HttpStatus.BAD_REQUEST, "PrivatelyOfferedFundAgencyAlreadyExists"),
    PrivatelyOfferedFundAgencyNotExists(HttpStatus.BAD_REQUEST, "PrivatelyOfferedFundAgencyNotExists"),

    PrivatelyOfferedFundChannelAlreadyExists(HttpStatus.BAD_REQUEST, "PrivatelyOfferedFundChannelAlreadyExists"),
    PrivatelyOfferedFundChannelNotExists(HttpStatus.BAD_REQUEST, "PrivatelyOfferedFundChannelNotExists"),

    InvalidDelete(HttpStatus.BAD_REQUEST, "InvalidDelete"),
    ;


    private final HttpStatus status;
    private final String code;

    EnumExceptionCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    public HttpStatus httpStatus() {
        return status;
    }

    public String getCode() {
        return StringUtils.isBlank(code) ? this.name() : this.code;
    }
}