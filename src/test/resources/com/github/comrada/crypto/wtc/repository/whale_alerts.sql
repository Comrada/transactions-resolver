INSERT INTO public.whale_alerts (id, message, link, posted_at, asset, amount, process_status,
                                 processed_at)
-- new
VALUES (1, '2,999 #PAXG (5,392,567 USD) transferred from #Binance to unknown wallet',
        'https://whale-alert.io/transaction/ethereum/1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9/1',
        '2021-12-20 10:04:01', 'PAXG', '2999', 'NEW', NULL),
       (10, '2,999 #PAXG (5,392,567 USD) transferred from #Binance to unknown wallet',
        'https://whale-alert.io/transaction/ethereum/1dace3b2d84e6e0372f323afba5c414156d7c0c72509e3be16eae63985612db9/1',
        '2021-12-20 10:04:02', 'PAXG', '2999', 'NEW', NULL),
-- failed
       (2, '3,999 #PAXG (7,190,100 USD) transferred from #Binance to unknown wallet',
        'https://whale-alert.io/transaction/ethereum/2206a09bdfbee5870005ccdfaafb70ba6b76127462689cad3e52e7c7a1f758a6/1',
        '2021-12-20 10:04:00', 'PAXG', '3999', 'FAILED', '2021-12-21 00:00:00'),
       (20, '3,999 #PAXG (7,190,100 USD) transferred from #Binance to unknown wallet',
        'https://whale-alert.io/transaction/ethereum/2206a09bdfbee5870005ccdfaafb70ba6b76127462689cad3e52e7c7a1f758a6/1',
        '2021-12-20 10:04:01', 'PAXG', '3999', 'FAILED', '2021-12-21 00:00:01'),
-- done
       (3, '60,000,000 #USDC (60,000,000 USD) transferred from unknown wallet to #CryptoCom',
        'https://whale-alert.io/transaction/ethereum/ca08ac7675440bc7e016504d7fdc18b52c0c144a60a19f729d61c9ed405a7d68',
        '2021-12-20 09:43:38', 'USDC', '60000000', 'DONE', '2021-12-21 00:00:10'),
       (30, '60,000,000 #USDC (60,000,000 USD) transferred from unknown wallet to #CryptoCom',
        'https://whale-alert.io/transaction/ethereum/ca08ac7675440bc7e016504d7fdc18b52c0c144a60a19f729d61c9ed405a7d68',
        '2021-12-20 09:43:39', 'USDC', '60000000', 'DONE', '2021-12-21 00:00:11');
