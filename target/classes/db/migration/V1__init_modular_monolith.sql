create table if not exists catalog_products (
  id bigserial primary key,
  sku varchar(100) not null unique,
  name varchar(255) not null,
  category varchar(120) not null,
  price numeric(12,2) not null
);

create table if not exists catalog_stores (
  id bigserial primary key,
  code varchar(50) not null unique,
  name varchar(255) not null,
  city varchar(120) not null
);

create table if not exists inventory_items (
  id bigserial primary key,
  sku varchar(100) not null unique,
  available int not null
);

create table if not exists cart_carts (
  id bigserial primary key,
  session_id varchar(100) not null unique
);

create table if not exists cart_items (
  id bigserial primary key,
  cart_id bigint not null,
  sku varchar(100) not null,
  quantity int not null,
  unit_price numeric(12,2) not null,
  constraint fk_cart_items_cart foreign key (cart_id) references cart_carts(id) on delete cascade
);

create table if not exists order_orders (
  id bigserial primary key,
  session_id varchar(100) not null,
  pickup_store_code varchar(50) not null,
  buyer_name varchar(255) not null,
  buyer_email varchar(255) not null,
  status varchar(30) not null,
  created_at timestamp with time zone not null
);

create table if not exists order_order_lines (
  id bigserial primary key,
  order_id bigint not null,
  sku varchar(100) not null,
  quantity int not null,
  constraint fk_order_lines_order foreign key (order_id) references order_orders(id) on delete cascade
);

create table if not exists order_outbox_events (
  id bigserial primary key,
  aggregate_type varchar(50) not null,
  aggregate_id varchar(80) not null,
  event_type varchar(100) not null,
  payload text not null,
  status varchar(30) not null,
  created_at timestamp with time zone not null
);

insert into catalog_products (sku, name, category, price) values
('TR-001', 'Tractor Alpha', 'tractors', 32000.00),
('TR-002', 'Tractor Beta', 'tractors', 41500.00),
('LD-001', 'Front Loader X', 'loaders', 12800.00)
on conflict (sku) do nothing;

insert into catalog_stores (code, name, city) values
('BOG-01', 'Tractor Store Bogota', 'Bogota'),
('MED-01', 'Tractor Store Medellin', 'Medellin')
on conflict (code) do nothing;

insert into inventory_items (sku, available) values
('TR-001', 12),
('TR-002', 8),
('LD-001', 21)
on conflict (sku) do nothing;
