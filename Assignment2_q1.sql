create view v1 as
select cust, prod, sum(quant) as sum, count(quant) as count
from sales
group by cust, prod;

create view CustProd as
select distinct a.cust, b.prod
from v1 a cross join v1 b;

create view SumCount1 as
select CustProd.cust, CustProd.prod, v1.sum, v1.count
from v1
right join
CustProd
on CustProd.cust = v1.cust and CustProd.prod = v1.prod;

create view SumCount2 as
select cust as cust2, prod as prod2, sum as sum2, count as count2
from SumCount1;

create view TheAvg as
select SumCount1.cust, SumCount1.prod, cast((cast(sum as numeric) / cast(count as numeric)) as bigint) as the_avg
from SumCount1;

create view OtherProdAvg as
select cust, prod, cast(sum(sum2) / sum(count2) as bigint) as other_prod_avg
from SumCount1
inner join SumCount2
on prod <> prod2 and cust = cust2
group by cust, prod;

create view OtherCustAvg as
select cust, prod, cast(sum(sum2) / sum(count2) as bigint) as other_cust_avg
from SumCount1
inner join SumCount2
on cust <> cust2 and prod = prod2
group by cust, prod;

create view Result1 as
select
TheAvg.cust,
TheAvg.prod,
the_avg,
other_prod_avg,
other_cust_avg
from ((
TheAvg
left join OtherCustAvg on OtherCustAvg.cust = TheAvg.cust and OtherCustAvg.prod = TheAvg.prod)
left join OtherProdAvg on OtherProdAvg.cust = TheAvg.cust and OtherProdAvg.prod = TheAvg.prod)
order by TheAvg.cust, TheAvg.prod;

select * from Result1;

/*
drop view Result1;
drop view OtherProdAvg;
drop view OtherCustAvg;
drop view TheAvg;
drop view SumCount2;
drop view SumCount1;
drop view CustProd;
drop view v1;
*/