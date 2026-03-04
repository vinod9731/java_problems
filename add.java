Calculate Daily Transaction Volume

SELECT
    PAY_TRANS_DT,
    COUNT(*) AS transaction_volume
FROM payment.default.payment_init
GROUP BY PAY_TRANS_DT
ORDER BY PAY_TRANS_DT;


Compute Average Transaction Volume


SELECT
    AVG(daily_count) AS avg_transactions
FROM (
    SELECT
        PAY_TRANS_DT,
        COUNT(*) AS daily_count
    FROM payment.default.payment_init
    GROUP BY PAY_TRANS_DT
) t;


Detect Volume Spikes

WITH daily_volume AS (
    SELECT
        PAY_TRANS_DT,
        COUNT(*) AS volume
    FROM payment.default.payment_init
    GROUP BY PAY_TRANS_DT
),
avg_volume AS (
    SELECT AVG(volume) AS avg_vol
    FROM daily_volume
)

SELECT
    d.PAY_TRANS_DT,
    d.volume
FROM daily_volume d
CROSS JOIN avg_volume a
WHERE d.volume > (a.avg_vol * 2)
ORDER BY d.PAY_TRANS_DT;



Detect Sudden Transaction Surges


SELECT
    PAY_TRANS_DT,
    COUNT(*) AS transaction_volume,
    LAG(COUNT(*)) OVER (ORDER BY PAY_TRANS_DT) AS previous_day_volume
FROM payment.default.payment_init
GROUP BY PAY_TRANS_DT
ORDER BY PAY_TRANS_DT;


now detect the surgs

WITH daily_volume AS (
    SELECT
        PAY_TRANS_DT,
        COUNT(*) AS volume
    FROM payment.default.payment_init
    GROUP BY PAY_TRANS_DT
)

SELECT
    PAY_TRANS_DT,
    volume,
    LAG(volume) OVER (ORDER BY PAY_TRANS_DT) AS prev_volume
FROM daily_volume
WHERE volume > 1.5 * LAG(volume) OVER (ORDER BY PAY_TRANS_DT);



Predict Future Transaction Volume


SELECT
    PAY_TRANS_DT,
    COUNT(*) AS volume,
    AVG(COUNT(*)) OVER (
        ORDER BY PAY_TRANS_DT
        ROWS BETWEEN 3 PRECEDING AND CURRENT ROW
    ) AS moving_avg_prediction
FROM payment.default.payment_init
GROUP BY PAY_TRANS_DT
ORDER BY PAY_TRANS_DT;

aml alert notification 

WITH daily_volume AS (
    SELECT
        PAY_TRANS_DT,
        COUNT(*) AS volume
    FROM payment.default.payment_init
    GROUP BY PAY_TRANS_DT
),
avg_volume AS (
    SELECT AVG(volume) AS avg_vol
    FROM daily_volume
)

SELECT
    d.PAY_TRANS_DT,
    d.volume,
    CASE
        WHEN d.volume > a.avg_vol * 2 THEN 'ALERT_SPIKE'
        WHEN d.volume > a.avg_vol * 1.5 THEN 'HIGH_VOLUME'
        ELSE 'NORMAL'
    END AS aml_alert
FROM daily_volume d
CROSS JOIN avg_volume a
ORDER BY d.PAY_TRANS_DT;
