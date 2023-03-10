create table categories
(
    id   bigint generated by default as identity,
    name varchar(255),
    primary key (id)
);

create table compilations
(
    id     bigint generated by default as identity,
    pinned boolean,
    title  varchar(255),
    primary key (id)
);

create table compilations_events
(
    compilation_id bigint not null,
    events_id      bigint not null,
    primary key (compilation_id, events_id)
);

create table events
(
    id                 bigint generated by default as identity,
    annotation         varchar(2000),
    compilation_id     bigint,
    confirmed_requests bigint,
    created_on         timestamp,
    description        varchar(7000),
    event_date         timestamp,
    paid               boolean,
    participant_limit  bigint,
    published_on       varchar(255),
    request_moderation boolean,
    state              varchar(255),
    state_action       int4,
    title              varchar(120),
    views              bigint,
    category_id        bigint,
    user_id            bigint,
    location_id        bigint,
    primary key (id)
);

create table locations
(
    id  bigint generated by default as identity,
    lat float4,
    lon float4,
    primary key (id)
);

create table requests
(
    id           bigint generated by default as identity,
    created      timestamp,
    event_id     bigint,
    requester_id bigint,
    status       int4,
    primary key (id)
);

create table users
(
    id    bigint generated by default as identity,
    email varchar(255),
    name  varchar(255),
    primary key (id)
);

alter table if exists categories
    add constraint uq_categories_name unique (name);

alter table if exists compilations_events
    add constraint uq_compilations_events unique (events_id);

alter table if exists requests
    add constraint uq_requests_event_id_and_requester_id unique (event_id, requester_id);

alter table if exists users
    add constraint uq_users_name unique (name);

alter table if exists compilations_events
    add constraint fk_compilations_events_events
        foreign key (events_id)
            references events;

alter table if exists compilations_events
    add constraint fk_compilations_events_compilations
        foreign key (compilation_id)
            references compilations;

alter table if exists events
    add constraint fk_event_categories
        foreign key (category_id)
            references categories;

alter table if exists events
    add constraint fk_event_users
        foreign key (user_id)
            references users;

alter table if exists events
    add constraint fk_event_locations
        foreign key (location_id)
            references locations;
