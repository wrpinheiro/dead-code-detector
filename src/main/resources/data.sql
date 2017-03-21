INSERT INTO repository(id, status, created_At, processed_At, repository_Url, repository_Name)
VALUES(1, 'ADDED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'user1/example_repo', 'Example Repo');

INSERT INTO repository(id, status, created_At, processed_At, repository_Url, repository_Name)
VALUES(2, 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'user1/another_repo', 'Another Repo');

INSERT INTO repository(id, status, created_At, processed_At, repository_Url, repository_Name)
VALUES(3, 'FAILED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'user1/failed_repo', 'Failed Repo');
