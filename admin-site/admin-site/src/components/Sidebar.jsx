import React from 'react';
import { FaUsers, FaBiking, FaBars, FaTimes } from 'react-icons/fa';
import { Link, useLocation } from 'react-router-dom';

const styles = {
  sidebar: {
    position: 'fixed',
    top: 0,
    left: 0,
    height: '100vh',
    width: '260px',
    background: 'linear-gradient(180deg, #7c3aed 0%, #5b21b6 100%)',
    color: 'white',
    boxShadow: '2px 0 10px rgba(0,0,0,0.1)',
    zIndex: 50,
    transition: 'transform 0.3s ease',
  },
  sidebarLogo: {
    padding: '1.5rem',
    borderBottom: '1px solid rgba(255,255,255,0.1)',
    fontSize: '1.5rem',
    fontWeight: '700',
    letterSpacing: '-0.025em',
  },
  sidebarSubtitle: {
    color: 'rgba(255,255,255,0.7)',
    fontSize: '0.75rem',
    marginTop: '0.25rem',
  },
  sidebarNav: {
    padding: '1rem',
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
  },
  menuItem: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
    padding: '0.75rem 1rem',
    borderRadius: '0.5rem',
    color: 'rgba(255,255,255,0.9)',
    textDecoration: 'none',
    fontWeight: '500',
    transition: 'all 0.2s ease',
    cursor: 'pointer',
  },
  menuItemActive: {
    background: 'rgba(255,255,255,0.2)',
    color: 'white',
    transform: 'scale(1.02)',
  },
  menuItemHover: {
    background: 'rgba(255,255,255,0.1)',
    transform: 'scale(1.02)',
  },
  icon: {
    width: '1.25rem',
    height: '1.25rem',
  },
  mobileToggle: {
    position: 'fixed',
    top: '1rem',
    left: '1rem',
    zIndex: 60,
    padding: '0.5rem',
    background: 'linear-gradient(90deg, #7c3aed, #a855f7)',
    color: 'white',
    borderRadius: '0.5rem',
    border: 'none',
    cursor: 'pointer',
    display: 'none',
  },
  overlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: 'rgba(0,0,0,0.5)',
    zIndex: 40,
  },
};

export default function Sidebar() {
  const location = useLocation();
  const [isOpen, setIsOpen] = React.useState(false);

  const menuItems = [
    { path: '/users', icon: FaUsers, label: 'Quản lý người dùng', color: '#a855f7' },
    { path: '/bikes', icon: FaBiking, label: 'Quản lý dịch vụ xe', color: '#a855f7' },
  ];

  return (
    <>
      <button 
        style={{...styles.mobileToggle, display: window.innerWidth < 768 ? 'block' : 'none'}}
        onClick={() => setIsOpen(!isOpen)}
      >
        {isOpen ? <FaTimes /> : <FaBars />}
      </button>
      
      <div 
        style={{
          ...styles.sidebar,
          transform: isOpen || window.innerWidth >= 768 ? 'translateX(0)' : 'translateX(-100%)',
        }}
      >
        <div style={styles.sidebarLogo}>
          <h2>MotorRenter</h2>
          <p style={styles.sidebarSubtitle}>Dashboard Admin</p>
        </div>
        <nav style={styles.sidebarNav}>
          {menuItems.map((item, index) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={index}
                to={item.path}
                style={{
                  ...styles.menuItem,
                  ...(isActive ? styles.menuItemActive : {}),
                }}
                onClick={() => setIsOpen(false)}
                onMouseEnter={(e) => { e.target.style.background = 'rgba(255,255,255,0.1)'; e.target.style.transform = 'scale(1.02)'; }}
                onMouseLeave={(e) => { if (!isActive) { e.target.style.background = 'transparent'; e.target.style.transform = 'scale(1)'; } }}
              >
                <Icon style={{...styles.icon, color: isActive ? item.color : 'white'}} />
                <span>{item.label}</span>
              </Link>
            );
          })}
        </nav>
      </div>
      
      {isOpen && window.innerWidth < 768 && <div style={styles.overlay} onClick={() => setIsOpen(false)} />}
      
      <style jsx>{`
        @media (max-width: 768px) {
          .mobile-toggle { display: block !important; }
        }
      `}</style>
    </>
  );
}