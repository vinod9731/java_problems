SELECT COUNT(*) AS unmatched_payments
FROM payment.default.payment_init p
LEFT JOIN payment.default.posting_init pos
ON CAST(p.PAY_OPE_NUM AS BIGINT) = pos.POS_OPERATION_NO
AND p.PAY_ACCOUNT_KEY = pos.POS_ACCOUNT_KEY
WHERE pos.POS_ID IS NULL;



SELECT p.PAY_ID,
       p.PAY_OPE_NUM,
       p.PAY_ACCOUNT_KEY
FROM payment.default.payment_init p
LEFT JOIN payment.default.posting_init pos
ON CAST(p.PAY_OPE_NUM AS BIGINT) = pos.POS_OPERATION_NO
AND p.PAY_ACCOUNT_KEY = pos.POS_ACCOUNT_KEY
WHERE pos.POS_ID IS NULL
LIMIT 20;


SELECT COUNT(*) AS unmatched_after
FROM payment.default.payment_after p
LEFT JOIN payment.default.posting_after pos
ON CAST(p.PAY_OPE_NUM AS BIGINT) = pos.POS_OPERATION_NO
AND p.PAY_ACCOUNT_KEY = pos.POS_ACCOUNT_KEY
WHERE pos.POS_ID IS NULL;



SELECT p.PAY_ID,
       p.PAY_INTRBKSTTLMAMT,
       pos.AML_SCOPE,
       pos.AML_EXCL_CODE
FROM payment.default.payment_after p
JOIN payment.default.posting_after pos
ON CAST(p.PAY_OPE_NUM AS BIGINT) = pos.POS_OPERATION_NO
AND p.PAY_ACCOUNT_KEY = pos.POS_ACCOUNT_KEY
WHERE p.PAY_INTRBKSTTLMAMT > 10000
AND (pos.AML_SCOPE IS NULL OR pos.AML_EXCL_CODE IS NULL);


SELECT COUNT(*) FROM payment.default.payment_init;
SELECT COUNT(*) FROM payment.default.posting_init;

SELECT COUNT(*) FROM payment.default.payment_after;
SELECT COUNT(*) FROM payment.default.posting_after;



SELECT COUNT(*) AS unmatched_payments
FROM payment.default.payment_init p
LEFT JOIN payment.default.posting_init pos
ON ltrim(p.PAY_OPE_NUM,'0') =
   ltrim(CAST(pos.POS_OPERATION_NO AS VARCHAR),'0')
AND p.PAY_ACCOUNT_KEY = pos.POS_ACCOUNT_KEY
WHERE pos.POS_ID IS NULL;

SELECT COUNT(*) AS unmatched_after
FROM payment.default.payment_after p
LEFT JOIN payment.default.posting_after pos
ON ltrim(p.PAY_OPE_NUM,'0') =
   ltrim(CAST(pos.POS_OPERATION_NO AS VARCHAR),'0')
AND p.PAY_ACCOUNT_KEY = pos.POS_ACCOUNT_KEY
WHERE pos.POS_ID IS NULL;

scre2

SELECT 
    p.PAY_ID,
    p.PAY_INTRBKSTTLMAMT,
    pos.AML_SCOPE,
    pos.AML_EXCL_CODE
FROM payment.default.payment_init p
JOIN payment.default.posting_init pos
ON ltrim(p.PAY_OPE_NUM,'0') =
   ltrim(CAST(pos.POS_OPERATION_NO AS VARCHAR),'0')
AND p.PAY_ACCOUNT_KEY = pos.POS_ACCOUNT_KEY

WHERE p.PAY_INTRBKSTTLMAMT > 10000
AND (
      pos.AML_SCOPE IS NULL
      OR pos.AML_EXCL_CODE IS NULL
);

SELECT COUNT(*) AS kyc_missing_transactions
FROM payment.default.payment_init p
JOIN payment.default.posting_init pos
ON ltrim(p.PAY_OPE_NUM,'0') =
   ltrim(CAST(pos.POS_OPERATION_NO AS VARCHAR),'0')
AND p.PAY_ACCOUNT_KEY = pos.POS_ACCOUNT_KEY

WHERE p.PAY_INTRBKSTTLMAMT > 10000
AND (
      pos.AML_SCOPE IS NULL
      OR pos.AML_EXCL_CODE IS NULL
);



SELECT COUNT(*) AS kyc_missing_after
FROM payment.default.payment_after p
JOIN payment.default.posting_after pos
ON ltrim(p.PAY_OPE_NUM,'0') =
   ltrim(CAST(pos.POS_OPERATION_NO AS VARCHAR),'0')
AND p.PAY_ACCOUNT_KEY = pos.POS_ACCOUNT_KEY

WHERE p.PAY_INTRBKSTTLMAMT > 10000
AND (
      pos.AML_SCOPE IS NULL
      OR pos.AML_EXCL_CODE IS NULL
);


scenario 1. 
       before 
       
SELECT COUNT(*) AS unmatched_payments
FROM payment.default.payment_init p
LEFT JOIN payment.default.posting_init pos
ON ltrim(p.PAY_OPE_NUM,'0') =
   ltrim(CAST(pos.POS_OPERATION_NO AS VARCHAR),'0')

AND p.PAY_ACCOUNT_KEY = pos.POS_ACCOUNT_KEY
AND p.PAY_TRANS_DT = pos.POS_DATE_DECLA_BATCH_INPUT
AND p.PAY_SERVICE_CD = pos.POS_DEPARTMENT_CODE
AND p.PAY_BULK_NUM = pos.POS_BATCH_NB

WHERE pos.POS_ID IS NULL;

scenario 1 after 

       ³
SELECT COUNT(*) AS unmatched_after
FROM payment.default.payment_after p
LEFT JOIN payment.default.posting_after pos
ON ltrim(p.PAY_OPE_NUM,'0') =
   ltrim(CAST(pos.POS_OPERATION_NO AS VARCHAR),'0')

AND p.PAY_ACCOUNT_KEY = pos.POS_ACCOUNT_KEY
AND p.PAY_TRANS_DT = pos.POS_DATE_DECLA_BATCH_INPUT
AND p.PAY_SERVICE_CD = pos.POS_DEPARTMENT_CODE
AND p.PAY_BULK_NUM = pos.POS_BATCH_NB

WHERE pos.POS_ID IS NULL;

SELECT COUNT(*)
FROM payment.default.payment_init
WHERE PAY_INTRBKSTTLMAMT > 10000;
