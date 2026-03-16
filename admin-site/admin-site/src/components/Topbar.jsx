import React from 'react';

const styles = {
  topbar: {
    background: 'white',
    boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
    position: 'sticky',
    top: 0,
    zIndex: 40,
    margin: 0,
    padding: 0,
  },
  topbarContent: {
    padding: '1rem 2rem',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    maxWidth: '1280px',
    margin: '0 auto',
    width: '100%',
    boxSizing: 'border-box',
  },
  searchContainer: {
    position: 'relative',
    width: '100%',
    maxWidth: '400px',
  },
  searchInput: {
    width: '100%',
    padding: '0.5rem 1rem 0.5rem 2.5rem',
    border: '1px solid #d1d5db',
    borderRadius: '0.5rem',
    fontSize: '0.875rem',
    background: '#f9fafb',
    transition: 'all 0.2s ease',
    outline: 'none',
  },
  searchInputFocus: {
    borderColor: '#7c3aed',
    boxShadow: '0 0 0 2px rgba(124,58,237,0.2)',
    background: 'white',
  },
  searchIcon: {
    position: 'absolute',
    left: '0.75rem',
    top: '50%',
    transform: 'translateY(-50%)',
    width: '1rem',
    height: '1rem',
    color: '#6b7280',
  },
  profileContainer: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
  },
  profileName: {
    color: '#374151',
    fontWeight: '500',
    fontSize: '0.875rem',
  },
  profileAvatar: {
    width: '2rem',
    height: '2rem',
    borderRadius: '50%',
    objectFit: 'cover',
    border: '2px solid #d1d5db',
    transition: 'all 0.2s ease',
  },
  profileAvatarHover: {
    borderColor: '#7c3aed',
    boxShadow: '0 0 0 2px rgba(124,58,237,0.2)',
  },
  onlineIndicator: {
    position: 'absolute',
    bottom: 0,
    right: 0,
    width: '0.5rem',
    height: '0.5rem',
    background: '#10b981',
    borderRadius: '50%',
    border: '2px solid white',
  },
};

export default function Topbar() {
  const [searchFocus, setSearchFocus] = React.useState(false);

  return (
    <div style={styles.topbar}>
      <div style={styles.topbarContent}>
        <div style={styles.searchContainer}>
          <svg style={styles.searchIcon} fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            type="text"
            placeholder="Tìm kiếm người dùng, dịch vụ..."
            style={{...styles.searchInput, ...(searchFocus ? styles.searchInputFocus : {})}}
            onFocus={() => setSearchFocus(true)}
            onBlur={() => setSearchFocus(false)}
          />
        </div>
        <div style={styles.profileContainer}>
          <span style={styles.profileName}>Anima Agrawal</span>
          <div style={{ position: 'relative' }}>
            <img 
              src="/avatar.png" 
              alt="avatar" 
              style={styles.profileAvatar}
              onMouseEnter={(e) => Object.assign(e.target.style, styles.profileAvatarHover)}
              onMouseLeave={(e) => Object.assign(e.target.style, styles.profileAvatar)}
            />
            <div style={styles.onlineIndicator} />
          </div>
        </div>
      </div>
      
      <style jsx>{`
        * {
          box-sizing: border-box;
        }
        body, html {
          margin: 0;
          padding: 0;
        }
      `}</style>
    </div>
  );
}