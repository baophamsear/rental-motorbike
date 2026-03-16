import { createContext, useReducer, useContext} from "react";
import MyReducer, { initialAuthState } from "./MyReducer"; 
import { jwtDecode } from "jwt-decode"; 

const MyUserContext = createContext();
const MyDispatchContext = createContext();

const initAuthState = () => {
  const storedAuth = localStorage.getItem("auth");
  if (storedAuth) {
    try {
      const { token } = JSON.parse(storedAuth);
      const decoded = jwtDecode(token);
      const role = decoded.roles?.[0]?.authority;
      const email = decoded.sub;
      console.log("Khôi phục đăng nhập từ localStorage:", { email, role });
      return {
        ...initialAuthState,
        isAuthenticated: true,
        user: { email, role },
        token
      };
    } catch (err) {
      console.error("Lỗi khi parse hoặc decode token:", err);
    }
  }
  return initialAuthState;
};

export function MyProvider({ children }) {
  const [state, dispatch] = useReducer(MyReducer, initialAuthState, initAuthState);

  return (
    <MyUserContext.Provider value={state}>
      <MyDispatchContext.Provider value={{ dispatch }}>
        {children}
      </MyDispatchContext.Provider>
    </MyUserContext.Provider>
  );
}

export const useMyState = () => useContext(MyUserContext);
export const useMyActions = () => useContext(MyDispatchContext);

export { MyUserContext, MyDispatchContext };