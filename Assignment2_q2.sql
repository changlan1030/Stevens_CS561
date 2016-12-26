create view CustProdMonthAvg as
select cust, prod, month, avg(quant) as avg
from sales
group by cust, prod, month;

create table Month
(month integer);
insert into Month values (1);
insert into Month values (2);
insert into Month values (3);
insert into Month values (4);
insert into Month values (5);
insert into Month values (6);
insert into Month values (7);
insert into Month values (8);
insert into Month values (9);
insert into Month values (10);
insert into Month values (11);
insert into Month values (12);

create view CustProdMonth as
select distinct cust, prod, Month.month
from CustProdMonthAvg
cross join Month;

create view BeforeAvg as
select
CustProdMonth.cust,
CustProdMonth.prod,
CustProdMonth.month,
cast(CustProdMonthAvg.avg as bigint) as before_avg
from CustProdMonthAvg
right outer join CustProdMonth
on CustProdMonthAvg.cust = CustProdMonth.cust and CustProdMonthAvg.prod = CustProdMonth.prod and CustProdMonthAvg.month = CustProdMonth.month - 1;

create view Result2 as
select
BeforeAvg.cust,
BeforeAvg.prod,
BeforeAvg.month,
BeforeAvg.before_avg,
cast(CustProdMonthAvg.avg as bigint) as after_avg
from CustProdMonthAvg
right outer join BeforeAvg
on CustProdMonthAvg.cust = BeforeAvg.cust and CustProdMonthAvg.prod = BeforeAvg.prod and CustProdMonthAvg.month = BeforeAvg.month + 1
order by BeforeAvg.cust, BeforeAvg.prod, BeforeAvg.month;

select * from Result2;

/*
drop view Result2;
drop view BeforeAvg;
drop view CustProdMonth;
drop view CustProdMonthAvg;
drop table Month;
*/