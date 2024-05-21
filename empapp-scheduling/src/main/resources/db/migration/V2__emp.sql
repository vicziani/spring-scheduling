create table address (id bigint not null auto_increment, city varchar(255), employee_id bigint, primary key (id));
create table employee (id bigint not null auto_increment, name varchar(255), primary key (id));
alter table address add constraint address_employee_fk foreign key (employee_id) references employee (id);