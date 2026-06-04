export const fetchWithAuth = async (url, options = {}) => {
  const user = JSON.parse(localStorage.getItem("user"));
  const token = user?.token;

  if (!token) {
    return null; 
  }

  const headers = {
    ...options.headers,
    Authorization: `Bearer ${token}`,
  };

  const res = await fetch(url, { ...options, headers });

  if (res.status === 401 || res.status === 403) {
    window.dispatchEvent(new Event("sessionExpired"));
    return null;
  }

  return res;
};
