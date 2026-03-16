import { useEffect, useState } from "react";
import { useDispatch, useSelector } from 'react-redux'   // <-- import
import { useNavigate } from 'react-router-dom'
import "../../assets/css/login.css";
import illustration from "../../assets/images/login-illustration.jpeg"; // đặt ảnh vào src/assets/
import {endpoints} from "../../context/APIs";
import APIs from "../../context/APIs";
import { useMyActions } from "../../context/MyContext";
import { AUTH } from "../../context/MyReducer";
import { jwtDecode } from "jwt-decode"; 



export default function Login() {
  const [email, setEmail] = useState("");
  const [pwd, setPwd] = useState("");
  const [showPwd, setShowPwd] = useState(false);
  const [agree, setAgree] = useState(false);

  const { dispatch } = useMyActions(); 
  const navigate = useNavigate();

  const onSubmit = async (e) => {
    e.preventDefault();
    if (!agree) return alert("Vui lòng đồng ý điều khoản.");

    try {
      const res = await APIs.post(endpoints['login'], { email, password: pwd });
      console.log("Login response:", res);
      const { user, token } = res.data;

      // Lưu vào localStorage (tùy chọn)
      localStorage.setItem("auth", JSON.stringify({ user, token }));
      localStorage.setItem("token", token);
      
      console.log("print token", token);

      console.log("Đăng nhập thành công:", user, token);
      const decoded = jwtDecode(token); 

      console.log("Decoded token:", decoded);
      const role = decoded.roles?.[0]?.authority;
      const emailFromToken = decoded.sub;
      console.log(decoded);

      localStorage.setItem("userId", decoded.userId); // Lưu userId
      console.log("Stored userId:", decoded.userId);

      if (role === "ROLE_admin") {
        dispatch({
          type: AUTH.LOGIN,
          user: { email: emailFromToken, role },
          token,
        });
        navigate("/users", { replace: true }); // nếu muốn redirect sau khi login
      } else {
        alert("Bạn không có quyền đăng nhập với tài khoản này.");
      }
    } catch (err) {
      console.error("Lỗi:", err.response?.data || err.message);
      alert("Đăng nhập thất bại: " + (err.response?.data?.message || "Lỗi không xác định"));
    }
  };


  return (
    <div className="login-page">
      {/* Cột trái: Illustration */}
      <aside className="login-left">
        <header className="brand">
          <div className="logo-dots">
            <span />
            <span />
            <span />
          </div>
          <span className="brand-text">AProjectO</span>
        </header>

        <div className="left-visual">
          <img src={illustration} alt="Login Illustration" />
        </div>
      </aside>

      {/* Cột phải: Form */}
      <main className="login-right">
        <div className="product-badge">✴︎ Asite Product System</div>

        <div className="form-wrap">
          <h1 className="title">Welcome back, Yash</h1>
          <p className="subtitle">Welcome back! Please enter your details.</p>

          <form className="login-form" onSubmit={onSubmit}>
            <label className="field">
              <span>Email</span>
              <input
                type="email"
                placeholder=""
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </label>

            <label className="field">
              <span>Password</span>
              <div className="pwd-box">
                <input
                  type={showPwd ? "text" : "password"}
                  value={pwd}
                  onChange={(e) => setPwd(e.target.value)}
                  required
                />
                <button
                  type="button"
                  className="pwd-toggle"
                  onClick={() => setShowPwd((s) => !s)}
                  aria-label="Toggle password visibility"
                >
                  {showPwd ? "🙈" : "👁️"}
                </button>
              </div>
            </label>

            <div className="row between">
              <label className="checkbox">
                <input
                  type="checkbox"
                  checked={agree}
                  onChange={(e) => setAgree(e.target.checked)}
                />
                <span>Terms & Conditions</span>
              </label>

              <a href="#" className="link">
                Forgot Password
              </a>
            </div>

            <button className="btn-primary" type="submit">
              Log in
            </button>

            <p className="signup">
              Don’t have an account? <a href="#" className="link">Sign up for free</a>
            </p>
          </form>
        </div>
      </main>
    </div>
  );
}
