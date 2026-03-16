import React, { useState, useEffect, useCallback, useContext } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  Image,
  SafeAreaView,
  StatusBar,
  ActivityIndicator,
  Alert,
  StyleSheet,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useNavigation } from '@react-navigation/native';
// import { getAuthApi } from '../../utils/useAuthApi';
// import { endpoints } from '../../configs/APIs';
import LinearGradient from 'react-native-linear-gradient'; // Correct import
import { endpoints } from '../configs/APIs';
import { getAuthApi } from '../utils/useAuthApi';
import { MyDispatchContext } from '../contexts/MyUserContext';

export default function ProfileScreen() {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const dispatch = useContext(MyDispatchContext);
  const navigation = useNavigation();

  const fetchUserProfile = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const api = await getAuthApi();
      const response = await api.get(endpoints['me']);
      setUser(response.data);
    } catch (err) {
      console.error('Error fetching user profile:', err);
      setError('Không thể tải thông tin hồ sơ. Vui lòng thử lại.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchUserProfile();
  }, [fetchUserProfile]);

  const handleLogout = useCallback(async () => {
    Alert.alert(
      'Đăng xuất',
      'Bạn có chắc chắn muốn đăng xuất?',
      [
        { text: 'Hủy', style: 'cancel' },
        {
          text: 'Đăng xuất',
          style: 'destructive',
          onPress: async () => {
            dispatch({
              type: 'logout',
            });
          },
        },
      ]
    );
  }, [navigation]);

  const displayRole = (role) => {
    if (!role) return 'N/A';
    return role.toLowerCase() === 'renter' ? 'Khách thuê' : role.toLowerCase() === 'lessor' ? 'Chủ xe' : role;
  };

  if (isLoading) {
    return (
      <SafeAreaView style={styles.safeArea}>
        <StatusBar barStyle="light-content" backgroundColor="#4CAF50" />
        <View style={styles.loadingContainer}>
          <Ionicons name="person-circle-outline" size={80} color="#4CAF50" />
          <Text style={styles.loadingText}>Đang tải hồ sơ...</Text>
          <ActivityIndicator size="large" color="#4CAF50" style={styles.loadingSpinner} />
        </View>
      </SafeAreaView>
    );
  }

  if (error) {
    return (
      <SafeAreaView style={styles.safeArea}>
        <StatusBar barStyle="light-content" backgroundColor="#4CAF50" />
        <View style={styles.errorContainer}>
          <Ionicons name="alert-circle-outline" size={80} color="#EF4444" />
          <Text style={styles.errorText}>{error}</Text>
          <TouchableOpacity style={styles.retryButton} onPress={fetchUserProfile} activeOpacity={0.7}>
            <Ionicons name="refresh" size={20} color="#fff" />
            <Text style={styles.retryButtonText}>Thử lại</Text>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="light-content" backgroundColor="#4CAF50" />
      {typeof LinearGradient === 'undefined' ? (
        <View style={styles.header}>
          <TouchableOpacity onPress={() => navigation.goBack()}>
            <Ionicons name="arrow-back" size={24} color="#fff" />
          </TouchableOpacity>
          <Text style={styles.headerTitle}>Hồ sơ cá nhân</Text>
        </View>
      ) : (
        <LinearGradient
          colors={['#4CAF50', '#66BB6A']}
          style={styles.header}
        >
          <TouchableOpacity onPress={() => navigation.goBack()}>
            <Ionicons name="arrow-back" size={24} color="#fff" />
          </TouchableOpacity>
          <Text style={styles.headerTitle}>Hồ sơ cá nhân</Text>
        </LinearGradient>
      )}
      <View style={styles.container}>
        <View style={styles.profileCard}>
          <Image
            source={{
              uri: user?.avatarUrl || 'https://via.placeholder.com/150',
            }}
            style={styles.avatar}
          />
          <Text style={styles.fullName}>{user?.fullName || 'N/A'}</Text>
          <Text style={styles.roleText}>{displayRole(user?.role)}</Text>
          <View style={styles.infoContainer}>
            <View style={styles.infoRow}>
              <Ionicons name="mail-outline" size={20} color="#4CAF50" />
              <Text style={styles.infoLabel}>Email</Text>
              <Text style={styles.infoValue}>{user?.email || 'N/A'}</Text>
            </View>
            <View style={styles.divider} />
            <View style={styles.infoRow}>
              <Ionicons name="call-outline" size={20} color="#4CAF50" />
              <Text style={styles.infoLabel}>Số điện thoại</Text>
              <Text style={styles.infoValue}>{user?.phone || 'Chưa cập nhật'}</Text>
            </View>
            <View style={styles.divider} />
            <View style={styles.infoRow}>
              <Ionicons name="person-outline" size={20} color="#4CAF50" />
              <Text style={styles.infoLabel}>Vai trò</Text>
              <Text style={styles.infoValue}>{displayRole(user?.role)}</Text>
            </View>
          </View>
        </View>
        <TouchableOpacity style={styles.logoutButton} onPress={handleLogout} activeOpacity={0.7}>
          <Ionicons name="log-out-outline" size={20} color="#fff" />
          <Text style={styles.logoutButtonText}>Đăng xuất</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#F9F9FB',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 16,
    paddingTop: 40,
    paddingBottom: 20,
    backgroundColor: '#4CAF50', // Fallback background
  },
  headerTitle: {
    fontSize: 22,
    fontWeight: '700',
    color: '#fff',
    marginLeft: 16,
  },
  container: {
    flex: 1,
    padding: 16,
    alignItems: 'center',
  },
  profileCard: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 24,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOpacity: 0.2,
    shadowRadius: 10,
    elevation: 6,
    marginBottom: 32,
    width: '100%',
    maxWidth: 400,
  },
  avatar: {
    width: 100,
    height: 100,
    borderRadius: 50,
    marginBottom: 16,
    borderWidth: 3,
    borderColor: '#4CAF50',
  },
  fullName: {
    fontSize: 26,
    fontWeight: '700',
    color: '#1F2A44',
    marginBottom: 8,
  },
  roleText: {
    fontSize: 16,
    fontWeight: '500',
    color: '#4CAF50',
    marginBottom: 16,
  },
  infoContainer: {
    width: '100%',
    backgroundColor: '#F9FAFB',
    borderRadius: 12,
    padding: 16,
  },
  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 8,
  },
  infoLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#6B7280',
    marginLeft: 12,
    width: 120,
  },
  infoValue: {
    fontSize: 16,
    fontWeight: '500',
    color: '#1F2A44',
    flex: 1,
  },
  divider: {
    height: 1,
    backgroundColor: '#E5E7EB',
    marginVertical: 8,
  },
  logoutButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#EF4444',
    borderRadius: 12,
    paddingVertical: 14,
    paddingHorizontal: 24,
    shadowColor: '#000',
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 4,
    width: '80%',
    maxWidth: 300,
  },
  logoutButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#fff',
    marginLeft: 8,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    fontSize: 18,
    fontWeight: '500',
    color: '#1F2A44',
    marginTop: 16,
    marginBottom: 8,
  },
  loadingSpinner: {
    marginTop: 8,
  },
  errorContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  errorText: {
    fontSize: 18,
    fontWeight: '500',
    color: '#EF4444',
    textAlign: 'center',
    marginVertical: 16,
  },
  retryButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#4CAF50',
    borderRadius: 12,
    paddingVertical: 14,
    paddingHorizontal: 24,
    shadowColor: '#000',
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 4,
  },
  retryButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#fff',
    marginLeft: 8,
  },
});