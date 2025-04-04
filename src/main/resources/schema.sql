create table carta (id bigint generated by default as identity, palo enum ('CORAZONES','DIAMANTES','PICAS','TREBOLES'), valor enum ('A','CINCO','CUATRO','DIEZ','DOS','J','K','NUEVE','OCHO','Q','SEIS','SIETE','TRES'), primary key (id));
create table jugador (activo boolean not null, fichas integer not null, mano_id bigint unique, id varchar(255) not null, mesa_id varchar(255), partida_id varchar(255), usuario_id varchar(255) not null, primary key (id));
create table mano (id bigint generated by default as identity, primary key (id));
create table mano_cartas (cartas_id bigint not null unique, mano_id bigint not null);
create table mesa (id varchar(255) not null, primary key (id));
create table partida (fin timestamp(6), inicio timestamp(6), id varchar(255) not null, id_ganador varchar(255), mesa_id varchar(255), estado enum ('EN_CURSO','FINALIZADA'), primary key (id));
create table partida_cartas_comunitarias (cartas_comunitarias_id bigint not null unique, partida_id varchar(255) not null);
create table usuario (activo boolean not null, dinero integer not null, fecha_nacimiento date, dni varchar(255), email varchar(255) unique, id varchar(255) not null, nombre_completo varchar(255), password_hash varchar(255), primary key (id));
alter table if exists jugador add constraint FKs4fuq5ydj7qpok16fkqjasi4k foreign key (mano_id) references mano;
alter table if exists jugador add constraint FKewpw391hnd9239bmjk9urk8i9 foreign key (mesa_id) references mesa;
alter table if exists jugador add constraint FK6yitgep48ek3mgx46s6oscky1 foreign key (partida_id) references partida;
alter table if exists jugador add constraint FKs8xmkcyi29yx1asmxpfaxl6wo foreign key (usuario_id) references usuario;
alter table if exists mano_cartas add constraint FK9drkor1cvusgsdg0tm4xcghl5 foreign key (cartas_id) references carta;
alter table if exists mano_cartas add constraint FKtr1p6opkh5bjgltkptak9bpsd foreign key (mano_id) references mano;
alter table if exists partida add constraint FKhh3ioi64n5eejsqq4sn7loeid foreign key (mesa_id) references mesa;
alter table if exists partida_cartas_comunitarias add constraint FKru9f1r96o0145x6c2tlvbr8mc foreign key (cartas_comunitarias_id) references carta;
alter table if exists partida_cartas_comunitarias add constraint FKfp3lmrmiq9hf4q4mbawmyysmq foreign key (partida_id) references partida;
create table carta (id bigint generated by default as identity, palo enum ('CORAZONES','DIAMANTES','PICAS','TREBOLES'), valor enum ('A','CINCO','CUATRO','DIEZ','DOS','J','K','NUEVE','OCHO','Q','SEIS','SIETE','TRES'), primary key (id));
create table jugador (activo boolean not null, fichas integer not null, mano_id bigint unique, id varchar(255) not null, mesa_id varchar(255), partida_id varchar(255), usuario_id varchar(255) not null, primary key (id));
create table mano (id bigint generated by default as identity, primary key (id));
create table mano_cartas (cartas_id bigint not null unique, mano_id bigint not null);
create table mesa (id varchar(255) not null, primary key (id));
create table partida (fin timestamp(6), inicio timestamp(6), id varchar(255) not null, id_ganador varchar(255), mesa_id varchar(255), estado enum ('EN_CURSO','FINALIZADA'), primary key (id));
create table partida_cartas_comunitarias (cartas_comunitarias_id bigint not null unique, partida_id varchar(255) not null);
create table usuario (activo boolean not null, dinero integer not null, fecha_nacimiento date, dni varchar(255), email varchar(255) unique, id varchar(255) not null, nombre_completo varchar(255), password_hash varchar(255), primary key (id));
alter table if exists jugador add constraint FKs4fuq5ydj7qpok16fkqjasi4k foreign key (mano_id) references mano;
alter table if exists jugador add constraint FKewpw391hnd9239bmjk9urk8i9 foreign key (mesa_id) references mesa;
alter table if exists jugador add constraint FK6yitgep48ek3mgx46s6oscky1 foreign key (partida_id) references partida;
alter table if exists jugador add constraint FKs8xmkcyi29yx1asmxpfaxl6wo foreign key (usuario_id) references usuario;
alter table if exists mano_cartas add constraint FK9drkor1cvusgsdg0tm4xcghl5 foreign key (cartas_id) references carta;
alter table if exists mano_cartas add constraint FKtr1p6opkh5bjgltkptak9bpsd foreign key (mano_id) references mano;
alter table if exists partida add constraint FKhh3ioi64n5eejsqq4sn7loeid foreign key (mesa_id) references mesa;
alter table if exists partida_cartas_comunitarias add constraint FKru9f1r96o0145x6c2tlvbr8mc foreign key (cartas_comunitarias_id) references carta;
alter table if exists partida_cartas_comunitarias add constraint FKfp3lmrmiq9hf4q4mbawmyysmq foreign key (partida_id) references partida;
