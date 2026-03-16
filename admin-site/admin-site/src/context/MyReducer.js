export const initialAuthState = {
  isAuthenticated: false,
  user: null,
  token: null,
  isLoading: false,
  error: null,
};

export const AUTH = {
  LOGIN: 'LOGIN',
  LOGOUT: 'LOGOUT',
};

export default function authReducer(state, action) {
  switch (action.type) {
    case AUTH.LOGIN:
      return { ...state, isLoading: false, isAuthenticated: true, user: action.user, token: action.token };
    case AUTH.LOGOUT:
      return { ...initialAuthState };
    default:
      return state;
  }
}