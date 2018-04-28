SELECT
  person_id,
  name,
  surname,
  email,
  password,
  role,
  phone,
  birthday
FROM public."Person"
WHERE lower(name) = ? OR lower(surname) = ?