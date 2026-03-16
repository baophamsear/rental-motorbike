import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';


export const endpoints = {
    'login' : '/auth/login',
    'users': '/users',
    'bikes': '/bikes/alls',
    'getPendingBikes' : '/bikes/pending',
    'bikes_status': '/bikes/status',
    'contracts': '/contracts/all',
    'update_available_bike': (id) => `/bikes/${id}/available`,
    'update_active_contract': (id) => `/contracts/${id}/active`,
    'init_multi': 'contracts/init-multiple',
    'notifications': 'notifications',
    'reject_contract': (id) => `/contracts/${id}/reject`,
}

export default axios.create({
    baseURL: BASE_URL
});

// Axios instance cho các yêu cầu có xác thực (có token)
export const authApis = (token) => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            'Authorization': `Bearer ${token}`,
            // 'Content-Type': 'application/json',  // Đảm bảo gửi dữ liệu ở định dạng JSON
        }
    });
};