-- function
CREATE FUNCTION set_current_timestamp_updated_at() RETURNS TRIGGER AS $$
DECLARE _new record;
BEGIN _new := NEW;
_new."updated_at" = clock_timestamp();
RETURN _new;
END;
$$ LANGUAGE plpgsql;
--------------- USER TABLE START -----------------------------------------------------------------------
create table if not exists public.users (
    id uuid not null primary key default gen_random_uuid(),
    email character varying unique not null,
    email_verified_at timestamptz,
    name character varying,
    image text,
    created_at timestamptz default clock_timestamp(),
    updated_at timestamptz default clock_timestamp()
);
-- this trigger will set the "updated_at" column to the current timestamptz for every update
CREATE TRIGGER handle_users_updated_at before
update on public.users for each row execute procedure set_current_timestamp_updated_at();
-- roles
CREATE TABLE if not exists public.roles (
    id uuid primary key default gen_random_uuid(),
    name varchar(150) not null unique,
    description text,
    created_at timestamptz default clock_timestamp(),
    updated_at timestamptz default clock_timestamp()
);
CREATE TRIGGER handle_roles_updated_at before
update on public.roles for each row execute procedure set_current_timestamp_updated_at();
-- permissions
CREATE TABLE if not exists public.permissions (
    id uuid primary key default gen_random_uuid(),
    name varchar(150) not null unique,
    description text,
    created_at timestamptz default clock_timestamp(),
    updated_at timestamptz default clock_timestamp()
);
CREATE TRIGGER handle_permissions_updated_at before
update on public.permissions for each row execute procedure set_current_timestamp_updated_at();
-- user roles
CREATE TABLE if not exists public.user_roles (
    user_id uuid references public.users on delete cascade on update cascade not null,
    role_id uuid references public.roles on delete cascade on update cascade not null,
    primary key (user_id, role_id)
);
-- roles permissions
create table if not exists public.role_permissions (
    role_id uuid references public.roles on delete cascade on update cascade not null,
    permission_id uuid references public.permissions on delete cascade on update cascade not null,
    primary key (role_id, permission_id)
);
-- auth
create or replace function not_empty(input text) returns boolean language plpgsql stable as $$ begin return (char_length(input) > 0);
end;
$$;
create table if not exists public.tokens (
    id uuid primary key default gen_random_uuid(),
    identifier text not null,
    expires timestamptz not null,
    value text not null unique,
    type text not null,
    created_at timestamptz not null default clock_timestamp(),
    updated_at timestamptz not null default clock_timestamp(),
    -- metadata jsonb,
    constraint tokens_type_identifier_token_not_empty check (
        not_empty(identifier)
        and not_empty(value)
        and not_empty(type)
    )
);
CREATE TRIGGER handle_tokens_updated_at before
update on public.tokens for each row execute procedure set_current_timestamp_updated_at();
-- -------------- USER ACCOUNTS TABLE START -----------------------------------------------------------------------
create table if not exists public.accounts (
    id uuid primary key default gen_random_uuid(),
    "user_id" uuid not null references public.users on delete cascade on update cascade,
    provider_id text not null,
    /**
     * This value depends on the type of the provider being used to create the account.
     * - oauth/oidc: The OAuth account's id, returned from the `profile()` callback.
     * - email: The user's email address.
     * - credentials: `id` returned from the `authorize()` callback
     */
    account_id varchar(255) not null,
    password_hash text,
    refresh_token text,
    access_token text,
    expires_at bigint,
    id_token text,
    scope text,
    session_state text,
    token_type text,
    created_at timestamptz default clock_timestamp(),
    updated_at timestamptz default clock_timestamp(),
    -- compound unique constraint on user_id and provider
--     constraint accounts_type_identifier_token_not_empty check (
--         char_length(type) > 0
--         and char_length(identifier) > 0
--         and char_length(token) > 0
--     ),
    -- constraint accounts_user_id_type_provider_account_id_not_empty check ("user_id", type, provider, "provider_account_id"),
    constraint accounts_provider_provider_account_id_unique unique (provider_id, account_id),
    constraint accounts_user_id_provider_unique unique ("user_id", provider_id)
);
CREATE TRIGGER handle_accounts_updated_at before
update on public.accounts for each row execute procedure set_current_timestamp_updated_at();
-- -------------- USER SESSIONS TABLE START -----------------------------------------------------------------------
create table if not exists public.sessions (
    id uuid primary key default gen_random_uuid(),
    "user_id" uuid not null references public.users on delete cascade on update cascade,
    token text not null unique,
    expires timestamptz not null,
    created_at timestamptz default clock_timestamp(),
    updated_at timestamptz default clock_timestamp(),
    constraint sessions_token_not_empty check (not_empty("token"))
);
CREATE TRIGGER handle_sessions_updated_at before
update on public.sessions for each row execute procedure set_current_timestamp_updated_at();