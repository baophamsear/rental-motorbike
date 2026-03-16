import React, { useEffect, useState, useRef } from "react";
import Sidebar from "../../components/Sidebar";
import Topbar from "../../components/Topbar";
import Footer from "../../components/Footer";
import mapboxgl from "mapbox-gl";
import "../../assets/css/contractStyles.css";
import { getAuthApi } from "../../config/authUtils";
import { endpoints } from "../../context/APIs";

// Mapbox Access Token
mapboxgl.accessToken = "pk.eyJ1IjoiYmFvcGhhbTAxMTAiLCJhIjoiY21leTc3dmdvMWVoNTJrcHlvY29xODZkYSJ9.vnT3usPvz6o6c-7X10sSmw";

export default function ContractManage() {
    const [contracts, setContracts] = useState([]);
    const [filteredContracts, setFilteredContracts] = useState([]);
    const [filterStatus, setFilterStatus] = useState('all');
    const [selectedContract, setSelectedContract] = useState(null);
    const [rejectingContract, setRejectingContract] = useState(null);
    const [rejectReason, setRejectReason] = useState("");
    const [rejectError, setRejectError] = useState("");
    const [zoomedImage, setZoomedImage] = useState(null);
    const mapContainer = useRef(null);
    const map = useRef(null);

    const fetchContracts = async () => {
        try {
            const api = await getAuthApi();
            const response = await api.get(endpoints['contracts']);
            const data = response.data || [
                {
                    contractId: 2,
                    lessor: {
                        userId: 4,
                        fullName: "Nguyễn Văn A",
                        email: "bao1@gmail.com",
                        phone: null,
                        role: "lessor",
                        avatarUrl: null,
                    },
                    bike: {
                        bikeId: 9,
                        owner: { fullName: "Nguyễn Văn A" },
                        brand: { brandId: 1, name: "Honda" },
                        licensePlate: [
                            "https://res.cloudinary.com/pqbou11/image/upload/v1756030975/chscuhlnfgulgorxdosc.jpg",
                            "https://res.cloudinary.com/pqbou11/image/upload/v1756030976/nm1ademfahpcm1axew32.jpg"
                        ],
                        imageUrl: [
                            "https://res.cloudinary.com/pqbou11/image/upload/v1756030972/aultwhulzkrl8jofa7cv.jpg",
                            "https://res.cloudinary.com/pqbou11/image/upload/v1756030973/vjicmxglfp1fl4bgml6f.jpg"
                        ],
                        status: "available",
                        pricePerDay: 100000.0,
                        location: { locationId: 1, name: "Tp. Hồ Chí Minh" },
                        name: "Honda wave 2020",
                        note: null,
                    },
                    serviceFee: 20000.0,
                    paymentCycle: "weekly",
                    startDate: "2025-08-21",
                    endDate: "2025-08-31",
                    status: "pending",
                    cancelRequestedAt: null,
                    approvedBy: null,
                    approvedAt: null,
                    updatedAt: "2025-08-28T18:04:58.099924",
                    location: {
                        id: 1,
                        latitude: 10.710464850657948,
                        longitude: 106.70901413963713,
                        address: "Vị trí được chọn trên bản đồ"
                    }
                },
                {
                    contractId: 3,
                    lessor: { fullName: "Trần Thị B" },
                    bike: { bikeId: 10, name: "Yamaha Sirius", pricePerDay: 120000, brand: { name: "Yamaha" }, location: { name: "Hà Nội" } },
                    serviceFee: 15000.0,
                    paymentCycle: "daily",
                    startDate: "2025-09-01",
                    endDate: "2025-09-05",
                    status: "updated",
                    location: { id: 2, latitude: 21.0285, longitude: 105.8542, address: "456 Cầu Giấy, Hà Nội" }
                }
            ];
            setContracts(data);
            setFilteredContracts(data);
        } catch (error) {
            console.error('Lỗi khi lấy danh sách hợp đồng:', error);
        }
    };

    useEffect(() => {
        fetchContracts();
    }, []);

    useEffect(() => {
        if (filterStatus === 'all') {
            setFilteredContracts(contracts);
        } else {
            setFilteredContracts(
                contracts.filter(contract => contract.status?.toLowerCase() === filterStatus)
            );
        }
    }, [filterStatus, contracts]);

    useEffect(() => {
        if (selectedContract && mapContainer.current && !map.current) {
            map.current = new mapboxgl.Map({
                container: mapContainer.current,
                style: 'mapbox://styles/mapbox/streets-v11',
                center: [selectedContract.location.longitude, selectedContract.location.latitude],
                zoom: 15
            });

            const popup = new mapboxgl.Popup({ offset: 25 })
                .setHTML(`
                    <h3>Vị trí hợp đồng</h3>
                    <p><strong>Địa chỉ:</strong> ${selectedContract.location.address}</p>
                    <p><strong>Tọa độ:</strong> Lat: ${selectedContract.location.latitude.toFixed(6)}, Lng: ${selectedContract.location.longitude.toFixed(6)}</p>
                `);

            new mapboxgl.Marker({
                color: '#7c3aed',
                scale: 1.5
            })
                .setLngLat([selectedContract.location.longitude, selectedContract.location.latitude])
                .setPopup(popup)
                .addTo(map.current);

            map.current.on('load', () => {
                popup.addTo(map.current);
            });

            return () => {
                if (map.current) map.current.remove();
            };
        }
    }, [selectedContract]);

    const updateActiveBike = async (bikeId) => {
        try {
            const api = await getAuthApi();
            await api.patch(endpoints['update_active_bike'](bikeId));
            fetchContracts();
        } catch (error) {
            console.error('Lỗi khi cập nhật trạng thái xe:', error);
        }
    };

    const updateBikeAvailability = async (bikeId) => {
        try {
            const api = await getAuthApi();
            await api.patch(endpoints['update_available_bike'](bikeId));
            fetchContracts();
        } catch (error) {
            console.error('Lỗi khi cập nhật tình trạng xe:', error);
        }
    };

    const handleApproveContract = async (contractId) => {
        try {
            const api = await getAuthApi();
            const contract = contracts.find(c => c.contractId === contractId);
            if (!contract || !contract.bike?.bikeId) {
                throw new Error('Hợp đồng hoặc xe không hợp lệ');
            }

            console.log('Xét duyệt hợp đồng:', contractId, 'cho xe:', contract.bike.bikeId);
            await api.patch(endpoints['update_active_contract'](contractId));
            await api.patch(endpoints['update_available_bike'](contract.bike.bikeId));
            console.log('Đã cập nhật tình trạng xe');
            console.log('Xét duyệt hợp đồng thành công');
            alert('Xét duyệt hợp đồng thành công');
            fetchContracts();
            setSelectedContract(null);
        } catch (error) {
            console.error('Lỗi xét duyệt hợp đồng:', error);
            alert('Lỗi khi xét duyệt hợp đồng: ' + error.message);
        }
    };

    const handleRejectContract = async (contractId) => {
        if (!rejectReason.trim()) {
            setRejectError('Vui lòng nhập lý do từ chối');
            return;
        }

        try {
            const api = await getAuthApi();
            console.log('Từ chối hợp đồng:', contractId, 'với lý do:', rejectReason);
            await api.patch(endpoints['reject_contract'](contractId), { rejectReason });
            console.log('Đã từ chối hợp đồng thành công');
            alert('Từ chối hợp đồng thành công');
            fetchContracts();
            setRejectingContract(null);
            setRejectReason("");
            setRejectError("");
            setSelectedContract(null);
        } catch (error) {
            console.error('Lỗi khi từ chối hợp đồng:', error);
            alert('Lỗi khi từ chối hợp đồng: ' + error.message);
        }
    };

    return (
        <div className="layout min-h-screen bg-gray-100 flex">
            <Sidebar />
            <div className="main-content flex-1 flex flex-col">
                <Topbar />
                <div className="page-content p-6 flex-1">
                    <div className="header mb-6">
                        <h2 className="main-header text-2xl font-bold text-gray-800">Quản lý hợp đồng</h2>
                        <p className="sub-header text-gray-600">Danh sách hợp đồng cần xử lý</p>
                        <div className="filter-bar flex items-center mt-4">
                            <label className="filter-label mr-2 text-gray-700">Hiển thị:</label>
                            <select
                                value={filterStatus}
                                onChange={(e) => setFilterStatus(e.target.value)}
                                className="filter-status border border-gray-300 rounded-md p-2 bg-white focus:ring-2 focus:ring-blue-500"
                            >
                                <option value="all">Tất cả</option>
                                <option value="pending">Pending</option>
                                <option value="active">Active</option>
                                <option value="completed">Completed</option>
                                <option value="cancelled">Cancelled</option>
                                <option value="updated">Updated</option>
                            </select>
                        </div>
                    </div>

                    {filteredContracts.length === 0 && (
                        <p className="empty-message text-gray-500 text-center">Không có hợp đồng nào.</p>
                    )}

                    <div className="contract-grid grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {filteredContracts.map((contract) => (
                            <div className="card bg-white shadow-md rounded-lg p-4" key={contract.contractId}>
                                <div className="card-header flex justify-between items-center">
                                    <div className="card-header-left">
                                        <strong className="bike-name text-lg font-semibold text-gray-800">{contract.bike?.name}</strong>
                                    </div>
                                    <span className={`status-badge px-2 py-1 rounded-full text-sm ${contract.status?.toLowerCase() === 'updated' ? 'bg-yellow-100 text-yellow-800' : 'bg-green-100 text-green-800'}`}>
                                        {contract.status}
                                    </span>
                                </div>

                                <div className="card-subinfo mt-3">
                                    <div className="subinfo-item flex justify-between">
                                        <span className="label font-medium text-gray-600">Người cho thuê:</span>
                                        <span>{contract.lessor?.fullName}</span>
                                    </div>
                                    <div className="subinfo-item flex justify-between mt-2">
                                        <span className="label font-medium text-gray-600">Địa điểm:</span>
                                        <span>{contract.location?.address}</span>
                                    </div>
                                </div>

                                <div className="card-meta mt-3">
                                    <div className="meta-item flex justify-between">
                                        <span className="label font-medium text-gray-600">Bắt đầu:</span>
                                        <span>{contract.startDate}</span>
                                    </div>
                                    <div className="meta-item flex justify-between mt-2">
                                        <span className="label font-medium text-gray-600">Kết thúc:</span>
                                        <span>{contract.endDate}</span>
                                    </div>
                                    <div className="meta-item price flex justify-between mt-2">
                                        <span className="label font-medium text-gray-600">Giá:</span>
                                        <span>{contract.bike?.pricePerDay?.toLocaleString()} VND/ngày</span>
                                    </div>
                                </div>

                                <div className="card-actions mt-4 flex space-x-2">
                                    <button
                                        className="btn primary bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600"
                                        onClick={() => setSelectedContract(contract)}
                                    >
                                        Chi tiết
                                    </button>
                                    {contract.status?.toLowerCase() === 'updated' && (
                                        <>
                                            <button
                                                className="btn approve bg-green-500 text-white px-4 py-2 rounded-md hover:bg-green-600"
                                                onClick={() => handleApproveContract(contract.contractId)}
                                            >
                                                Xét duyệt
                                            </button>
                                            <button
                                                className="btn reject bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600"
                                                onClick={() => setRejectingContract(contract)}
                                            >
                                                Từ chối
                                            </button>
                                        </>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>

                    {selectedContract && (
                        <div className="modal-overlay fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center" onClick={() => setSelectedContract(null)}>
                            <div className="modal-content bg-white rounded-lg p-6 max-w-2xl w-full max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                                <h3 className="modal-header text-xl font-bold text-gray-800 mb-4">{selectedContract.bike?.name}</h3>
                                <div className="modal-section mb-4">
                                    <h4 className="modal-sub-header text-lg font-semibold text-gray-700">Thông tin hợp đồng</h4>
                                    <div className="modal-info-grid grid grid-cols-1 md:grid-cols-2 gap-2">
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">ID hợp đồng:</span>
                                            <span>{selectedContract.contractId}</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Trạng thái:</span>
                                            <span className={`status-badge px-2 py-1 rounded-full text-sm ${selectedContract.status?.toLowerCase() === 'updated' ? 'bg-yellow-100 text-yellow-800' : 'bg-green-100 text-green-800'}`}>
                                                {selectedContract.status}
                                            </span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Phí dịch vụ:</span>
                                            <span>{selectedContract.serviceFee?.toLocaleString()} VND</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Chu kỳ thanh toán:</span>
                                            <span>{selectedContract.paymentCycle}</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Ngày bắt đầu:</span>
                                            <span>{selectedContract.startDate}</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Ngày kết thúc:</span>
                                            <span>{selectedContract.endDate}</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Cập nhật lúc:</span>
                                            <span>{selectedContract.updatedAt || 'Chưa cập nhật'}</span>
                                        </div>
                                    </div>
                                </div>
                                <div className="modal-section mb-4">
                                    <h4 className="modal-sub-header text-lg font-semibold text-gray-700">Thông tin người cho thuê</h4>
                                    <div className="modal-info-grid grid grid-cols-1 md:grid-cols-2 gap-2">
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Họ tên:</span>
                                            <span>{selectedContract.lessor?.fullName}</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Email:</span>
                                            <span>{selectedContract.lessor?.email}</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Điện thoại:</span>
                                            <span>{selectedContract.lessor?.phone || 'Chưa cung cấp'}</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Vai trò:</span>
                                            <span>{selectedContract.lessor?.role}</span>
                                        </div>
                                    </div>
                                </div>
                                <div className="modal-section mb-4">
                                    <h4 className="modal-sub-header text-lg font-semibold text-gray-700">Thông tin xe</h4>
                                    <div className="modal-info-grid grid grid-cols-1 md:grid-cols-2 gap-2">
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Tên xe:</span>
                                            <span>{selectedContract.bike?.name}</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Hãng xe:</span>
                                            <span>{selectedContract.bike?.brand?.name}</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Giá thuê/ngày:</span>
                                            <span>{selectedContract.bike?.pricePerDay?.toLocaleString()} VND</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Địa điểm xe:</span>
                                            <span>{selectedContract.bike?.location?.name}</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Trạng thái xe:</span>
                                            <span>{selectedContract.bike?.status}</span>
                                        </div>
                                        <div className="modal-info-item flex justify-between">
                                            <span className="modal-label font-medium text-gray-600">Ghi chú:</span>
                                            <span>{selectedContract.bike?.note || 'Không có'}</span>
                                        </div>
                                    </div>
                                </div>
                                <div className="modal-section mb-4">
                                    <h4 className="modal-sub-header text-lg font-semibold text-gray-700">Hình ảnh xe</h4>
                                    <div className="image-grid grid grid-cols-2 gap-2">
                                        {selectedContract.bike?.imageUrl?.map((url, index) => (
                                            <img
                                                key={index}
                                                src={url}
                                                alt={`bike-image-${index}`}
                                                className="preview-image w-full h-32 object-cover rounded-md"
                                                onClick={() => setZoomedImage(url)}
                                            />
                                        ))}
                                    </div>
                                </div>
                                <div className="modal-section mb-4">
                                    <h4 className="modal-sub-header text-lg font-semibold text-gray-700">Ảnh biển số xe</h4>
                                    <div className="image-grid grid grid-cols-2 gap-2">
                                        {selectedContract.bike?.licensePlate?.map((url, index) => (
                                            <img
                                                key={index}
                                                src={url}
                                                alt={`plate-image-${index}`}
                                                className="preview-image w-full h-32 object-cover rounded-md"
                                                onClick={() => setZoomedImage(url)}
                                            />
                                        ))}
                                    </div>
                                </div>
                                <div className="modal-section mb-4">
                                    <h4 className="modal-sub-header text-lg font-semibold text-gray-700">Vị trí trên bản đồ</h4>
                                    <div ref={mapContainer} className="map-container h-64 rounded-md" />
                                </div>
                                <div className="modal-actions flex space-x-2">
                                    {selectedContract.status?.toLowerCase() === 'updated' && (
                                        <>
                                            <button
                                                className="btn approve bg-green-500 text-white px-4 py-2 rounded-md hover:bg-green-600"
                                                onClick={() => handleApproveContract(selectedContract.contractId)}
                                            >
                                                Xét duyệt
                                            </button>
                                            <button
                                                className="btn reject bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600"
                                                onClick={() => setRejectingContract(selectedContract)}
                                            >
                                                Từ chối
                                            </button>
                                        </>
                                    )}
                                    <button className="btn close bg-gray-500 text-white px-4 py-2 rounded-md hover:bg-gray-600" onClick={() => setSelectedContract(null)}>
                                        Đóng
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}

                    {rejectingContract && (
                        <div className="modal-overlay fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center" onClick={() => setRejectingContract(null)}>
                            <div className="modal-content bg-white rounded-lg p-6 max-w-lg w-full" onClick={(e) => e.stopPropagation()}>
                                <h3 className="modal-header text-xl font-bold text-gray-800 mb-4">Từ chối hợp đồng</h3>
                                <div className="mb-6">
                                    <label className="block text-sm font-semibold text-gray-800 mb-2">
                                        Lý do từ chối
                                    </label>
                                    <div className="relative">
                                        <textarea
                                            className={`w-full rounded-xl border ${rejectError ? 'border-red-500' : 'border-gray-300'
                                                } focus:ring-2 focus:ring-red-500 focus:border-transparent p-4 text-gray-700 shadow-sm transition`}
                                            value={rejectReason}
                                            onChange={(e) => {
                                                setRejectReason(e.target.value);
                                                setRejectError("");
                                            }}
                                            placeholder="Nhập lý do từ chối hợp đồng..."
                                            rows={5}
                                            maxLength={500}
                                        />
                                        <div className="absolute bottom-2 right-3 text-xs text-gray-400">
                                            {rejectReason.length}/500 ký tự
                                        </div>
                                    </div>
                                    {rejectError && (
                                        <p className="mt-1 text-sm text-red-600">{rejectError}</p>
                                    )}
                                </div>

                                <div className="modal-actions flex justify-end space-x-2 mt-6">
                                    <button
                                        className="btn primary bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600"
                                        onClick={() => handleRejectContract(rejectingContract.contractId)}
                                    >
                                        Xác nhận
                                    </button>
                                    <button
                                        className="btn close bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600"
                                        onClick={() => {
                                            setRejectingContract(null);
                                            setRejectReason("");
                                            setRejectError("");
                                        }}
                                    >
                                        Hủy
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}

                    {zoomedImage && (
                        <div className="zoom-overlay fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center" onClick={() => setZoomedImage(null)}>
                            <img src={zoomedImage} alt="zoomed" className="zoomed-image max-w-[90%] max-h-[90%]" />
                        </div>
                    )}
                </div>
                <Footer />
            </div>
        </div>
    );
}