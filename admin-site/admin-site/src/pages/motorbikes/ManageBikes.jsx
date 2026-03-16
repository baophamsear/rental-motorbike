// ManageBikes.jsx
import React from 'react';
import "../../assets/css/vehicleservices.css";
import IconRequirement from '../../assets/icons/vespa-scooter.svg';
import IconEdit from '../../assets/icons/edit.svg';
import IconTemp from '../../assets/icons/temp.svg';
import Sidebar from '../../components/Sidebar';
import Topbar from '../../components/Topbar';
import Footer from '../../components/Footer';
import { useNavigate } from 'react-router-dom';  // ✅ dùng đúng hook

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
    marginLeft: '260px',
    width: 'calc(100% - 260px)',
    marginTop: 0,
    paddingTop: 0,
  },
  pageContent: {
    flex: 1,
    padding: '1.5rem 2rem',
    maxWidth: '1280px',
    margin: '0 auto',
    width: '100%',
    boxSizing: 'border-box',
  },
  header: {
    marginBottom: '1rem',
  },
  mainHeader: {
    fontSize: '2rem',
    fontWeight: '700',
    color: '#1f2937',
    lineHeight: '2.5rem',
    margin: '0 0 0.5rem 0',
  },
  subHeader: {
    fontSize: '1rem',
    color: '#6b7280',
    fontWeight: '400',
    margin: '0 0 1.5rem 0',
  },
  servicesContainer: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
    gap: '1.5rem',
  },
  serviceCard: {
    background: 'white',
    borderRadius: '0.75rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    padding: '1.5rem',
    transition: 'all 0.3s ease',
    cursor: 'pointer',
    animation: 'fadeIn 0.5s ease-out forwards',
    opacity: 0,
    transform: 'translateY(15px)',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    textAlign: 'center',
    border: '1px solid #e5e7eb',
  },
  serviceCardHover: {
    transform: 'translateY(-4px)',
    boxShadow: '0 4px 8px rgba(0,0,0,0.15)',
    borderColor: '#7c3aed',
  },
  iconBox: {
    width: '4rem',
    height: '4rem',
    marginBottom: '1rem',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'linear-gradient(135deg, #f3e8ff, #e0e7ff)',
    borderRadius: '0.5rem',
    transition: 'all 0.3s ease',
  },
  icon: {
    width: '2.5rem',
    height: '2.5rem',
    objectFit: 'contain',
  },
  cardTitle: {
    fontSize: '1.25rem',
    fontWeight: '600',
    color: '#1f2937',
    margin: '0 0 0.75rem 0',
  },
  cardDesc: {
    fontSize: '0.875rem',
    color: '#6b7280',
    lineHeight: '1.5',
    margin: 0,
    maxWidth: '280px',
  },
};

export default function ManageBikes() {
  const nav = useNavigate();

  const services = [
    {
      icon: IconRequirement,
      title: 'Quản lý yêu cầu đăng kí thông tin xe',
      desc: 'Xử lý các yêu cầu đăng kí thông tin xe một cách nhanh chóng và hiệu quả.',
      onClick: () => nav('/bikes-requirements'),
    },
    {
      icon: IconEdit,
      title: 'Quản lý hợp đồng cho thuê xe',
      desc: 'Chúng tôi hỗ trợ quản lý hợp đồng cho thuê xe một cách nhanh chóng và chính xác.',
      onClick: () => nav('/contracts-manage'),
    },
    {
      icon: IconTemp,
      title: 'Dịch vụ bảo trì',
      desc: 'Đội ngũ kỹ thuật hỗ trợ bảo trì xe ngay tại nơi bạn yêu cầu.',
    },
  ];

  return (
    <div style={styles.layout}>
      <Sidebar />
      <div style={styles.mainContent}>
        <Topbar />
        <div style={styles.pageContent}>
          <div style={styles.header}>
            <h2 style={styles.mainHeader}>Dịch vụ xe</h2>
            <p style={styles.subHeader}>Quản lý dịch vụ xe máy hiệu quả</p>
          </div>
          <div style={styles.servicesContainer}>
            {services.map((service, index) => (
              <div
                key={index}
                style={styles.serviceCard}
                onClick={service.onClick}
                onMouseEnter={(e) => Object.assign(e.target.style, styles.serviceCardHover)}
                onMouseLeave={(e) => Object.assign(e.target.style, styles.serviceCard)}
              >
                <div style={styles.iconBox}>
                  <img src={service.icon} alt={service.title} style={styles.icon} />
                </div>
                <h3 style={styles.cardTitle}>{service.title}</h3>
                <p style={styles.cardDesc}>{service.desc}</p>
              </div>
            ))}
          </div>
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
        * {
          box-sizing: border-box;
        }
        body, html {
          margin: 0;
          padding: 0;
        }
        #root {
          margin: 0;
          padding: 0;
        }
      `}</style>
    </div>
  );
}