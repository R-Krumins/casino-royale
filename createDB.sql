create database stocks;

create table stocks (
	symbol varchar(8) primary key,
	company_name varchar(64) not null,
	industry varchar(64),
	description text
);

create table priceHistory (
	date date,
	symbol varchar(8),
	price double precision
);