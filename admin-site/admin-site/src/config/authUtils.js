import { authApis } from '../context/APIs';

export const getAuthApi = async () => {
  const token = localStorage.getItem("token");
  console.log("Auth token:", token);
  return authApis(token);
};
