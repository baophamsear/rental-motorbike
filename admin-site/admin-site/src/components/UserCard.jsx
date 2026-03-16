import React from 'react';

const styles = {
  userCard: {
    background: 'white',
    borderRadius: '0.75rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    transition: 'all 0.3s ease',
    borderLeft: '4px solid transparent',
    animation: 'fadeIn 0.5s ease-out forwards',
    opacity: 0,
    transform: 'translateY(15px)',
  },
  userCardHover: {
    transform: 'translateY(-2px)',
    boxShadow: '0 4px 8px rgba(0,0,0,0.15)',
  },
  userCardActive: {
    borderLeftColor: '#7c3aed',
  },
  userCardContent: {
    padding: '1.25rem',
  },
  userHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '1rem',
  },
  userName: {
    fontSize: '1.125rem',
    fontWeight: '600',
    color: '#1f2937',
    margin: 0,
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  },
  suspendedBadge: {
    background: '#fef2f2',
    color: '#dc2626',
    padding: '0.25rem 0.75rem',
    borderRadius: '9999px',
    fontSize: '0.75rem',
    fontWeight: '500',
  },
  avatarContainer: {
    display: 'flex',
    justifyContent: 'center',
    marginBottom: '1rem',
  },
  avatar: {
    width: '3rem',
    height: '3rem',
    borderRadius: '50%',
    objectFit: 'cover',
    border: '2px solid #f3f4f6',
    transition: 'all 0.2s ease',
  },
  avatarHover: {
    transform: 'scale(1.05)',
    borderColor: '#e0e7ff',
  },
  userInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
    fontSize: '0.875rem',
  },
  infoRow: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
  },
  infoLabel: {
    color: '#6b7280',
    width: '4rem',
    fontWeight: '500',
    flexShrink: 0,
  },
  infoValue: {
    color: '#1f2937',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  },
  roleBadge: {
    padding: '0.25rem 0.75rem',
    borderRadius: '9999px',
    fontSize: '0.75rem',
    fontWeight: '600',
  },
  roleAdmin: {
    background: '#f3e8ff',
    color: '#7c3aed',
  },
  roleUser: {
    background: '#ecfdf5',
    color: '#059669',
  },
  actionButtons: {
    marginTop: '1rem',
    paddingTop: '1rem',
    borderTop: '1px solid #f3f4f6',
    display: 'flex',
    gap: '0.5rem',
    justifyContent: 'flex-end',
  },
  actionButton: {
    padding: '0.375rem 0.75rem',
    border: 'none',
    borderRadius: '0.5rem',
    fontSize: '0.75rem',
    fontWeight: '500',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
  },
  editButton: {
    background: '#f3e8ff',
    color: '#7c3aed',
  },
  editButtonHover: {
    background: '#e9d5ff',
    transform: 'translateY(-1px)',
  },
  deleteButton: {
    background: '#fef2f2',
    color: '#dc2626',
  },
  deleteButtonHover: {
    background: '#fee2e2',
    transform: 'translateY(-1px)',
  },
};

export default function UserCard({ user, isActive }) {
  const [hovered, setHovered] = React.useState(false);
  const [avatarHovered, setAvatarHovered] = React.useState(false);

  return (
    <div 
      style={{
        ...styles.userCard,
        ...(hovered ? styles.userCardHover : {}),
        ...(isActive ? styles.userCardActive : {}),
      }}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
    >
      <div style={styles.userCardContent}>
        <div style={styles.userHeader}>
          <h3 style={styles.userName}>{user.fullName}</h3>
          {user.suspended && <span style={styles.suspendedBadge}>Bị khóa</span>}
        </div>
        <div style={styles.avatarContainer}>
          <img
            src={user.avatarUrl || '/avatar.png'}
            alt={user.fullName}
            style={{...styles.avatar, ...(avatarHovered ? styles.avatarHover : {})}}
            onMouseEnter={() => setAvatarHovered(true)}
            onMouseLeave={() => setAvatarHovered(false)}
          />
        </div>
        <div style={styles.userInfo}>
          <p style={styles.infoRow}>
            <span style={styles.infoLabel}>Email:</span>
            <span style={styles.infoValue}>{user.email}</span>
          </p>
          <p style={styles.infoRow}>
            <span style={styles.infoLabel}>SĐT:</span>
            <span style={styles.infoValue}>{user.phone}</span>
          </p>
          <p style={styles.infoRow}>
            <span style={styles.infoLabel}>Vai trò:</span>
            <span style={{
              ...styles.roleBadge,
              ...(user.role === 'Admin' ? styles.roleAdmin : styles.roleUser),
            }}>
              {user.role}
            </span>
          </p>
        </div>
        <div style={styles.actionButtons}>
          <button 
            style={{...styles.actionButton, ...styles.editButton}}
            onMouseEnter={(e) => Object.assign(e.target.style, styles.editButtonHover)}
            onMouseLeave={(e) => Object.assign(e.target.style, styles.editButton)}
          >
            Sửa
          </button>
          <button 
            style={{...styles.actionButton, ...styles.deleteButton}}
            onMouseEnter={(e) => Object.assign(e.target.style, styles.deleteButtonHover)}
            onMouseLeave={(e) => Object.assign(e.target.style, styles.deleteButton)}
          >
            Xóa
          </button>
        </div>
      </div>
      
      <style jsx>{`
        @keyframes fadeIn {
          0% { opacity: 0; transform: translateY(15px); }
          100% { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </div>
  );
}