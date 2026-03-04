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
