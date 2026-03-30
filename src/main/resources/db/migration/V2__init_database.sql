
INSERT INTO users (created_at, last_login, updated_at, id, username, first_name, last_name, `password`, `role`) VALUES (
    now(), null, null, 1, '${ADMIN_USERNAME}', 'Onion', 'Ring', '${ADMIN_PASSWORD_HASH}', 'LIBRARIAN'
);

INSERT INTO librarians VALUES (
    1, 'HEAD_LIBRARIAN'
);

INSERT INTO authors (created_at, id, updated_at, first_name, last_name, full_name, `type`) VALUES (
    now(), 1, null, 'UNKNOWN', 'AUTHOR', 'UNKNOWN AUTHOR', 'INDIVIDUAL'
);