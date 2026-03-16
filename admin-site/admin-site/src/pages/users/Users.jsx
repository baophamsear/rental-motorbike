import React from 'react';
import Sidebar from '../../components/Sidebar';
import Topbar from '../../components/Topbar';
import UserCard from '../../components/UserCard';
import Footer from '../../components/Footer';
import APIs, { authApis, endpoints } from '../../context/APIs';
import { getAuthApi } from '../../config/authUtils';

const styles = {
  layout: {
    minHeight: '100vh',
    background: '#f9fafb',
    fontFamily: "'Inter', sans-serif",
    display: 'flex',
    overflow: 'hidden',
  },
  mainContent: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    marginLeft: '260px', // Match Sidebar width
    width: 'calc(100% - 260px)',
  },
  pageContent: {
    flex: 1,
    padding: '1.5rem 2rem',
    maxWidth: '1280px',
    margin: '0 auto',
    width: '100%',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '1.5rem',
  },
  title: {
    fontSize: '2rem',
    fontWeight: '700',
    color: '#1f2937',
    lineHeight: '2.5rem',
  },
  addButton: {
    padding: '0.5rem 1.25rem',
    background: 'linear-gradient(90deg, #7c3aed, #a855f7)',
    color: 'white',
    border: 'none',
    borderRadius: '0.5rem',
    fontSize: '0.875rem',
    fontWeight: '600',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  addButtonHover: {
    transform: 'translateY(-1px)',
    boxShadow: '0 4px 8px rgba(0,0,0,0.15)',
    background: 'linear-gradient(90deg, #6d28d9, #9333ea)',
  },
  userGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
    gap: '1.5rem',
  },
  emptyState: {
    textAlign: 'center',
    padding: '3rem',
    background: 'white',
    borderRadius: '0.75rem',
    boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
    color: '#6b7280',
    fontSize: '1rem',
  },
};

export default function Users() {
  const [users, setUsers] = React.useState([]);

  React.useEffect(() => {
    const fetchUsers = async () => {
      try {
        const api = await getAuthApi();
        const response = await api.get(endpoints['users']);
        setUsers(response.data || [
          { id: 1, fullName: "Nguyễn Văn A", email: "a@example.com", phone: "+84123456789", role: "Admin", avatarUrl: "/avatar.png", suspended: false },
          { id: 2, fullName: "Trần Thị B", email: "b@example.com", phone: "+84987654321", role: "User", avatarUrl: "/avatar.png", suspended: true },
        ]);
      } catch (error) {
        console.error('Error fetching user data:', error);
        setUsers([
          { id: 1, fullName: "Nguyễn Văn A", email: "a@example.com", phone: "+84123456789", role: "Admin", avatarUrl: "/avatar.png", suspended: false },
          { id: 2, fullName: "Trần Thị B", email: "b@example.com", phone: "+84987654321", role: "User", avatarUrl: "/avatar.png", suspended: true },
        ]);
      }
    };
    fetchUsers();
  }, []);

  const handleAddClick = () => {
    alert('Chức năng thêm người dùng sẽ được triển khai sau!');
  };

  return (
    <div style={styles.layout}>
      <Sidebar />
      <div style={styles.mainContent}>
        <Topbar />
        <div style={styles.pageContent}>
          <div style={styles.header}>
            <h2 style={styles.title}>Quản lý Người Dùng</h2>
            <button 
              style={styles.addButton}
              onClick={handleAddClick}
              onMouseEnter={(e) => Object.assign(e.target.style, styles.addButtonHover)}
              onMouseLeave={(e) => Object.assign(e.target.style, styles.addButton)}
            >
              Thêm Người Dùng
            </button>
          </div>
          {users.length > 0 ? (
            <div style={styles.userGrid}>
              {users.map((user, i) => (
                <UserCard key={user.id} user={user} isActive={i === 0} />
              ))}
            </div>
          ) : (
            <div style={styles.emptyState}>
              Chưa có dữ liệu người dùng.
            </div>
          )}
        </div>
        <Footer />
      </div>
      
      <style jsx>{`
        @keyframes fadeIn {
          0% { opacity: 0; transform: translateY(15px); }
          100% { opacity: 1; transform: translateY(0); }
        }
        @media (max-width: 768px) {
          [data-main-content] {
            margin-left: 0 !important;
            width: 100% !important;
          }
          [data-page-content] {
            padding: 1rem !important;
          }
        }
      `}</style>
    </div>
  );
}