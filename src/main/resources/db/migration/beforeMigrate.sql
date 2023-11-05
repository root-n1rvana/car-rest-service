DO $$
    DECLARE
        r RECORD;
    BEGIN
        FOR r IN (
            SELECT tablename FROM pg_tables WHERE schemaname = current_schema()
                                              AND tablename IN ('models', 'brands', 'types', 'models_types')
        ) LOOP
                EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' RESTART IDENTITY CASCADE';
            END LOOP;
    END $$;