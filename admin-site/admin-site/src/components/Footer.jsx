import React from 'react';
import { FaFacebookF, FaXTwitter, FaInstagram, FaLinkedinIn } from 'react-icons/fa6';

const styles = {
  footer: {
    background: 'linear-gradient(180deg, #1f2937 0%, #111827 100%)',
    color: 'white',
    padding: '2rem',
    marginTop: '2rem',
  },
  footerContent: {
    maxWidth: '1280px',
    margin: '0 auto',
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
    gap: '1.5rem',
  },
  footerSection: {
    display: 'flex',
    flexDirection: 'column',
  },
  footerTitle: {
    fontSize: '1.125rem',
    fontWeight: '600',
    marginBottom: '1rem',
    color: '#a855f7',
  },
  footerDescription: {
    color: '#d1d5db',
    lineHeight: '1.5',
    fontSize: '0.875rem',
    marginBottom: '1rem',
    maxWidth: '280px',
  },
  socialIcons: {
    display: 'flex',
    gap: '0.75rem',
  },
  socialIcon: {
    width: '1.25rem',
    height: '1.25rem',
    color: '#d1d5db',
    transition: 'all 0.2s ease',
    cursor: 'pointer',
  },
  socialIconHover: {
    color: '#a855f7',
    transform: 'translateY(-2px)',
  },
  footerLinkList: {
    listStyle: 'none',
    padding: 0,
    margin: 0,
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
  },
  footerLink: {
    color: '#d1d5db',
    textDecoration: 'none',
    fontSize: '0.875rem',
    transition: 'all 0.2s ease',
  },
  footerLinkHover: {
    color: '#a855f7',
    paddingLeft: '0.25rem',
  },
  contactInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
    color: '#d1d5db',
    fontSize: '0.875rem',
  },
  contactLabel: {
    fontWeight: '600',
    color: '#f9fafb',
  },
  footerBottom: {
    borderTop: '1px solid #374151',
    paddingTop: '1rem',
    textAlign: 'center',
    color: '#9ca3af',
    fontSize: '0.75rem',
    marginTop: '1.5rem',
  },
};

export default function Footer() {
  const socialLinks = [
    { icon: FaFacebookF, href: '#' },
    { icon: FaXTwitter, href: '#' },
    { icon: FaInstagram, href: '#' },
    { icon: FaLinkedinIn, href: '#' },
  ];

  const quickLinks = ['Trang Chủ', 'Về Chúng Tôi', 'Dịch Vụ', 'Tin Tức', 'Liên Hệ'];
  const menuItems = ['Thuê Xe Máy', 'Bảo Dưỡng Xe', 'Thuê Theo Giờ', 'Thuê Dài Hạn', 'Dịch Vụ Giao Xe'];

  return (
    <footer style={styles.footer}>
      <div style={styles.footerContent}>
        <div style={styles.footerSection}>
          <h3 style={styles.footerTitle}>MotorRenter</h3>
          <p style={styles.footerDescription}>
            Nền tảng cho thuê xe máy hàng đầu, mang đến trải nghiệm di chuyển tiện lợi và an toàn.
          </p>
          <div style={styles.socialIcons}>
            {socialLinks.map((link, index) => (
              <a 
                key={index} 
                href={link.href} 
                style={styles.socialIcon}
                onMouseEnter={(e) => e.target.style.transform = 'translateY(-2px)'}
                onMouseLeave={(e) => e.target.style.transform = 'translateY(0)'}
              >
                <link.icon />
              </a>
            ))}
          </div>
        </div>
        <div style={styles.footerSection}>
          <h4 style={styles.footerTitle}>Liên Kết Nhanh</h4>
          <ul style={styles.footerLinkList}>
            {quickLinks.map((item, index) => (
              <li key={index}>
                <a 
                  href="#" 
                  style={styles.footerLink}
                  onMouseEnter={(e) => e.target.style.paddingLeft = '0.25rem'}
                  onMouseLeave={(e) => e.target.style.paddingLeft = '0'}
                >
                  {item}
                </a>
              </li>
            ))}
          </ul>
        </div>
        <div style={styles.footerSection}>
          <h4 style={styles.footerTitle}>Dịch Vụ</h4>
          <ul style={styles.footerLinkList}>
            {menuItems.map((item, index) => (
              <li key={index}>
                <a 
                  href="#" 
                  style={styles.footerLink}
                  onMouseEnter={(e) => e.target.style.paddingLeft = '0.25rem'}
                  onMouseLeave={(e) => e.target.style.paddingLeft = '0'}
                >
                  {item}
                </a>
              </li>
            ))}
          </ul>
        </div>
        <div style={styles.footerSection}>
          <h4 style={styles.footerTitle}>Liên Hệ</h4>
          <div style={styles.contactInfo}>
            <p><span style={styles.contactLabel}>Địa chỉ:</span> 123 Đường Chính, Quận 1, TP.HCM</p>
            <p><span style={styles.contactLabel}>Điện thoại:</span> +84 123 456 789</p>
            <p><span style={styles.contactLabel}>Email:</span> info@motorrenter.com</p>
          </div>
        </div>
      </div>
      <div style={styles.footerBottom}>
        <p>&copy; 2025 MotorRenter. Tất cả quyền được bảo lưu.</p>
      </div>
    </footer>
  );
}