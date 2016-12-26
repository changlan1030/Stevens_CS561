create view ProdMonthQ as
select prod, month, quant
from sales;

create view ProdMonthMaxAvg as
select prod, month, max(quant) as max, avg(quant) as avg
from ProdMonthQ
group by prod, month;

create view BeforeTot as
select ProdMonthMaxAvg.prod, ProdMonthMaxAvg.month, count(quant) as before_tot
from ProdMonthQ
right outer join ProdMonthMaxAvg
on ProdMonthQ.prod = ProdMonthMaxAvg.prod and ProdMonthQ.month = ProdMonthMaxAvg.month - 1 and quant >= avg and quant <= max
group by ProdMonthMaxAvg.prod, ProdMonthMaxAvg.month;

create view AfterTot as
select ProdMonthMaxAvg.prod, ProdMonthMaxAvg.month, count(quant) as after_tot
from ProdMonthQ
right outer join ProdMonthMaxAvg
on ProdMonthQ.prod = ProdMonthMaxAvg.prod and ProdMonthQ.month = ProdMonthMaxAvg.month + 1 and quant >= avg and quant <= max
group by ProdMonthMaxAvg.prod, ProdMonthMaxAvg.month;

create view Result3 as
select BeforeTot.prod, BeforeTot.month, before_tot, after_tot
from BeforeTot
right outer join AfterTot
on BeforeTot.prod = AfterTot.prod and BeforeTot.month = AfterTot.month
order by prod, month;

select * from Result3;

/*
drop view Result3;
drop view AfterTot;
drop view BeforeTot;
drop view ProdMonthMaxAvg;
drop view ProdMonthQ;
*/