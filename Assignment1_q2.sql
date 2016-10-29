create table ct_max as
select cust, prod, quant, date from (
select cust, prod, quant, right('0' || cast(month as varchar(2)), 2) || '/' || right('0' || cast(day as varchar(2)), 2) || '/' || cast(year as varchar) as date,
ROW_NUMBER() over (partition by cust, prod order by quant desc) num
from sales
where state = 'CT' and year between 2000 and 2005) a
where num = 1;

create table ny_min as
select cust, prod, quant, date from (
select cust, prod, quant, right('0' || cast(month as varchar(2)), 2) || '/' || right('0' || cast(day as varchar(2)), 2) || '/' || cast(year as varchar) as date,
ROW_NUMBER() over (partition by cust, prod order by quant) num
from sales
where state = 'NY') a
where num = 1;

create table nj_min as
select cust, prod, quant, date from (
select cust, prod, quant, right('0' || cast(month as varchar(2)), 2) || '/' || right('0' || cast(day as varchar(2)), 2) || '/' || cast(year as varchar) as date,
ROW_NUMBER() over (partition by cust, prod order by quant) num
from sales
where state = 'NJ') a
where num = 1;

create table cust_prod as
select cust, prod from ct_max
union
select cust, prod from ny_min
union
select cust, prod from nj_min;

create table result2 as
select
cust_prod.cust,
cust_prod.prod,
ct_max.quant as ct_max,
ct_max.date as ct_date,
ny_min.quant as ny_min,
ny_min.date as ny_date,
nj_min.quant as nj_min,
nj_min.date as nj_date
from ((
cust_prod
left join ct_max on cust_prod.cust = ct_max.cust and cust_prod.prod = ct_max.prod)
left join ny_min on cust_prod.cust = ny_min.cust and cust_prod.prod = ny_min.prod)
left join nj_min on cust_prod.cust = nj_min.cust and cust_prod.prod = nj_min.prod;

select * from result2;

/*
drop table ct_max;
drop table ny_min;
drop table nj_min;
drop table cust_prod;
drop table result2;
*/