create table max as
select prod, quant, cust, state, right('0' || cast(month as varchar(2)), 2) || '/' || right('0' || cast(day as varchar(2)), 2) || '/' || cast(year as varchar) as date
from (
select prod, quant, cust, state, year, month, day, ROW_NUMBER() over (partition by sales.prod order by sales.quant desc) num
from sales) a
where num = 1;

create table min as
select prod, quant, cust, state, right('0' || cast(month as varchar(2)), 2) || '/' || right('0' || cast(day as varchar(2)), 2) || '/' || cast(year as varchar) as date
from (
select prod, quant, cust, year, month, day, state, ROW_NUMBER() over (partition by sales.prod order by sales.quant) num
from sales) a
where num = 1;

create table avg as
select prod, avg(quant) as avg_q
from sales
group by prod;

create table result1 as
select
max.prod,
max.quant as max_q,
max.cust as max_cust,
max.date as max_date,
max.state as max_st,
min.quant as min_q,
min.cust as min_cust,
min.date as min_date,
min.state as min_st,
avg_q
from (
max
left join min on max.prod = min.prod)
left join avg on max.prod = avg.prod;

select * from result1;

/*
drop table max;
drop table min;
drop table avg;
drop table result1;
*/