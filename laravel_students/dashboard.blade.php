@extends('layouts.app')

@section('content')
<style>
    /* gunakan Poppins agar konsisten dengan admin */
    @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap');
    body { font-family: 'Poppins', sans-serif; background-color: #f8f9fc; }

    /* ===== Stats Card ===== */
    .dashboard-stats {
        display: flex;
        gap: 30px;
        flex-wrap: wrap;
        justify-content: center;
        margin-bottom: 20px;
    }

    .stat-card {
        background: #ffffff;
        border-radius: 14px;
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.06);
        padding: 22px 28px;
        width: 230px;
        text-align: center;
        transition: transform 0.3s ease, box-shadow 0.3s ease;
        opacity: 0;
        transform: translateY(18px);
        animation: fadeInUp 0.6s forwards;
    }

    .stat-icon {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 60px;
        height: 60px;
        border-radius: 50%;
        margin: 0 auto 15px;
        font-size: 1.5rem;
        color: #fff;
        transition: transform 0.3s ease;
    }

    /* Warna gradient berbeda untuk setiap stat-card berdasarkan nth-child */
    .dashboard-stats .stat-card:nth-child(1) .stat-icon {
        background: linear-gradient(135deg, #2980b9, #3498db);
    }
    .dashboard-stats .stat-card:nth-child(2) .stat-icon {
        background: linear-gradient(135deg, #27ae60, #2ecc71);
    }
    .dashboard-stats .stat-card:nth-child(3) .stat-icon {
        background: linear-gradient(135deg, #f39c12, #e67e22);
    }
    .dashboard-stats .stat-card:nth-child(4) .stat-icon {
        background: linear-gradient(135deg, #e74c3c, #c0392b);
    }

    .stat-title {
        font-size: 0.95rem;
        font-weight: 600;
        margin-bottom: 8px;
        color: #555;
        letter-spacing: 0.3px;
    }

    .stat-value {
        font-size: 2rem;
        font-weight: 700;
        color: #2980b9;
    }

    .stat-card:hover {
        transform: translateY(-6px);
        box-shadow: 0 10px 26px rgba(0, 0, 0, 0.09);
    }

    .stat-card:hover .stat-icon {
        transform: scale(1.1) rotate(5deg);
    }

    /* ===== Card umum ===== */
    .card {
        background: #fff;
        border-radius: 14px;
        box-shadow: 0 4px 14px rgba(0, 0, 0, 0.05);
        margin-bottom: 30px;
        border: none;
        opacity: 0;
        transform: translateY(18px);
        animation: fadeInUp 0.7s forwards;
    }

    .card-header {
        padding: 16px 20px;
        background: linear-gradient(90deg, #f8f9fc, #eef3fb);
        font-weight: 600;
        font-size: 1rem;
        color: #2c3e50;
        border-bottom: 1px solid #e0e6f1;
    }

    .card-body { padding: 20px; }

    /* ===== Table ===== */
    .table {
        width: 100%;
        border-collapse: collapse;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 4px 15px rgba(0, 123, 255, 0.08);
    }

    .table thead {
        background: linear-gradient(90deg, #0062cc, #0056b3);
        color: #fff;
    }

    .table th, .table td {
        padding: 14px 16px;
        font-size: 0.95rem;
        text-align: left;
    }

    .table tbody tr:nth-child(even) { background-color: #f9fbfd; }
    .table tbody tr:hover { background-color: #eef5ff; transition: 0.2s; }

    /* ===== Badges (Bootstrap-compatible classes kept) ===== */
    .badge {
        display: inline-block;
        padding: 6px 10px;
        border-radius: 6px;
        font-weight: 600;
        font-size: 0.85rem;
    }
    .badge-success { background: linear-gradient(90deg,#28a745,#218838); color: #fff; }
    .badge-warning { background: linear-gradient(90deg,#f39c12,#d68910); color: #fff; }
    .badge-danger  { background: linear-gradient(90deg,#dc3545,#c82333); color: #fff; }
    .badge-info    { background: linear-gradient(90deg,#17a2b8,#117a8b); color: #fff; }

    /* ===== Buttons kecil untuk aksi ===== */
    .btn-sm {
        padding: 6px 12px;
        font-size: 0.85rem;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        font-weight: 600;
        transition: all 0.2s ease;
    }

    /* ===== Animation Keyframes ===== */
    @keyframes fadeInUp {
        from { opacity: 0; transform: translateY(18px); }
        to   { opacity: 1; transform: translateY(0); }
    }

    /* responsive tweaks to keep tables readable on small screens */
    @media (max-width: 768px) {
        .stat-card { width: calc(50% - 20px); }
    }
    @media (max-width: 420px) {
        .stat-card { width: 100%; }
    }

    .my-title{
        text-align: center;
        padding: 25px;
    }
    .flip-clock {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
}

.flip-clock .digit {
    background: linear-gradient(145deg, #2c3e50, #34495e);
    color: #fff;
    font-size: 2.2rem;
    font-weight: 600;
    padding: 16px 14px;
    border-radius: 10px;
    min-width: 55px;
    text-align: center;
    box-shadow: 0 4px 14px rgba(0, 0, 0, 0.25);
    transition: all 0.2s ease-in-out;
}

.flip-clock .digit:hover {
    transform: scale(1.08);
    background: linear-gradient(145deg, #34495e, #2c3e50);
}

.flip-clock .separator {
    font-size: 2.2rem;
    font-weight: 700;
    color: #2c3e50;
    padding: 0 4px;
}

</style>

<div class="container-fluid">
    <!-- Page Heading -->
    <div class="d-sm-flex align-items-center justify-content-between mb-4" style="position: relative;">
        <h1 class="h3 mb-0 text-gray-800 my-title" style="flex-grow: 1;">Halo {{ Auth::user()->name }}, Selamat Datang di 4llAset</h1>
        <div class="notification-wrapper" style="position: absolute; top: 0; right: 0; cursor: pointer;">
            <i id="notification-bell" class="fas fa-bell fa-2x" style="color: #2980b9; position: relative;"></i>
            @if(isset($unreadCount) && $unreadCount > 0)
                <span id="notification-badge" style="position: absolute; top: -5px; right: -5px; background: red; color: white; border-radius: 50%; padding: 3px 7px; font-size: 0.75rem;">
                    {{ $unreadCount }}
                </span>
            @endif
            <div id="notification-dropdown" style="display: none; position: absolute; right: 0; top: 30px; background: white; width: 300px; max-height: 400px; overflow-y: auto; box-shadow: 0 4px 8px rgba(0,0,0,0.1); border-radius: 8px; z-index: 1000;">
                @if(isset($notifications) && count($notifications) > 0)
                    <ul style="list-style: none; margin: 0; padding: 10px;">
@foreach($notifications as $notification)
    @php
        $studentName = $notification->sender ? $notification->sender->name : 'Unknown';
        $studentProfile = $notification->sender && $notification->sender->profile_picture ? $notification->sender->profile_picture : null;
        $message = $notification->message ?? 'Notifikasi baru';
    @endphp
    <li style="padding: 8px; border-bottom: 1px solid #eee; cursor: pointer; font-weight: {{ $notification->is_read ? 'normal' : 'bold' }};" data-id="{{ $notification->id }}" data-link="{{ $notification->link }}">
        <img src="{{ $studentProfile && $studentProfile !== 'uploads/profile_pictures/default.png' ? asset('storage/profile_pictures/' . $studentProfile) : asset('uploads/profile_pictures/default.png') }}" alt="Avatar" style="width: 30px; height: 30px; border-radius: 50%; margin-right: 10px; vertical-align: middle;">
        <strong>{{ $studentName }}</strong>: {{ $message }}
        <br>
        <small style="color: #888;">{{ $notification->created_at->diffForHumans() }}</small>
    </li>
@endforeach
                    </ul>
                @else
                    <p style="padding: 10px; color: #666;">Tidak ada notifikasi baru.</p>
                @endif
            </div>
        </div>
    </div>

    <!-- Stats (SAFE fallbacks: gunakan variabel yang tersedia jika $totalAset tidak diset) -->
    <!-- Pastikan sudah ada link Font Awesome -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<div class="dashboard-stats">
    <div class="stat-card" style="animation-delay:0.1s;">
        <div class="stat-icon"><i class="fas fa-book-reader"></i></div>
        <div>
            <div class="stat-title">Peminjaman Aktif</div>
            <div class="stat-value">
                {{ $activeBorrowings ?? $peminjamanAktif ?? 0 }}
            </div>
        </div>
    </div>

    <div class="stat-card" style="animation-delay:0.2s;">
        <div class="stat-icon"><i class="fas fa-boxes"></i></div>
        <div>
            <div class="stat-title">Total Aset</div>
            <div class="stat-value">
                {{ $totalAset ?? $totalAssets ?? 0 }}
            </div>
        </div>
    </div>

    <div class="stat-card" style="animation-delay:0.3s;">
        <div class="stat-icon"><i class="fas fa-hourglass-half"></i></div>
        <div>
            <div class="stat-title">Menunggu Persetujuan</div>
            <div class="stat-value">
                {{ $pendingBorrowings ?? $menungguPersetujuan ?? 0 }}
            </div>
        </div>
    </div>

    <div class="stat-card" style="animation-delay:0.4s;">
        <div class="stat-icon"><i class="fas fa-undo-alt"></i></div>
        <div>
            <div class="stat-title">Belum Dikembalikan</div>
            <div class="stat-value">
                {{ $rejectedBorrowings ?? $belumDikembalikan ?? 0 }}
            </div>
        </div>
    </div>
</div>


    <!-- Active Borrowings -->
<div class="row">
    <!-- Peminjaman Aktif -->
    <div class="col-xl-8 col-lg-7">
        <div class="card shadow mb-4" style="animation-delay:0.5s;">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Peminjaman Aktif</h6>
            </div>
            <div class="card-body">
                @if(!empty($activeBorrowingsList) && count($activeBorrowingsList) > 0)
                    <div class="table-responsive">
                        <table class="table table-bordered">
                            <thead>
                                <tr>
                                    <th>Aset</th>
                                    <th>Tgl Pinjam</th>
                                    <th>Tgl Kembali</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                @foreach($activeBorrowingsList as $borrowing)
                                <tr>
                                    <td>
                                        <ul style="list-style-type: none; padding: 0;">
                                            @foreach($borrowing->commodities as $commodity)
                                                <li>{{ $commodity->name }} ({{ $commodity->pivot->quantity }})</li>
                                            @endforeach
                                        </ul>
                                    </td>
                                    <td>{{ $borrowing->borrow_date }}</td>
                                    <td>{{ $borrowing->return_date }}</td>
                                    <td>
                                        @php $st = $borrowing->status ?? 'pending'; @endphp
                                        <span class="badge {{ $st == 'approved' ? 'badge-success' : ($st == 'pending' ? 'badge-warning' : 'badge-danger') }}">
                                            {{ ucfirst($st) }}
                                        </span>
                                    </td>
                                </tr>
                                @endforeach
                            </tbody>
                        </table>
                    </div>
                @else
                    <p class="text-muted">Tidak ada peminjaman aktif saat ini.</p>
                @endif
            </div>
        </div>
    </div>

    <!-- Jam -->
    <div class="col-xl-4 col-lg-5">
        <div class="card shadow mb-4 clock-card" style="animation-delay:0.6s;">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Waktu Saat Ini</h6>
            </div>
            <div class="card-body d-flex justify-content-center align-items-center">
                <div class="flip-clock">
                    <div class="digit" id="hours">00</div>
                    <span class="separator">:</span>
                    <div class="digit" id="minutes">00</div>
                    <span class="separator">:</span>
                    <div class="digit" id="seconds">00</div>
                </div>
            </div>
        </div>
    </div>
</div>


        <!-- Recent Requests -->
        <div class="col-xl-4 col-lg-5">
            <div class="card shadow mb-4" style="animation-delay:0.6s;">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">Permintaan Terbaru</h6>
                </div>
                <div class="card-body">
                    @if(!empty($recentRequests) && count($recentRequests) > 0)
                        <div class="table-responsive">
                            <table class="table table-bordered">
                                <thead>
                                    <tr>
                                        <th>Aset</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    @foreach($recentRequests as $request)
                                    <tr>
                                        <td>
                                            <ul style="list-style-type: none; padding: 0;">
                                                @foreach($request->commodities as $commodity)
                                                    <li>{{ $commodity->name }} ({{ $commodity->pivot->quantity }})</li>
                                                @endforeach
                                            </ul>
                                        </td>
                                        <td>
                                            @php $rs = $request->status ?? 'pending'; @endphp
                                            <span class="badge {{ $rs == 'approved' ? 'badge-success' : ($rs == 'pending' ? 'badge-warning' : 'badge-danger') }}">
                                                {{ ucfirst($rs) }}
                                            </span>
                                        </td>
                                    </tr>
                                    @endforeach
                                </tbody>
                            </table>
                        </div>
                    @else
                        <p class="text-muted">Belum ada permintaan peminjaman.</p>
                    @endif
                </div>
            </div>
        </div>
    </div>

    <!-- Borrowing History -->
    <div class="row">
        <div class="col-12">
            <div class="card shadow mb-4" style="animation-delay:0.7s;">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">Riwayat Peminjaman</h6>
                </div>
                <div class="card-body">
                    @if(!empty($borrowingHistory) && count($borrowingHistory) > 0)
                        <div class="table-responsive">
                            <table class="table table-bordered">
                                <thead>
                                    <tr>
                                        <th>Aset</th>
                                        <th>Tgl Pinjam</th>
                                        <th>Tgl Kembali</th>
                                        <th>Status</th>
                                        <th>Catatan</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    @foreach($borrowingHistory as $history)
                                    <tr>
                                        <td>
                                            <ul style="list-style-type: none; padding: 0;">
                                                @foreach($history->commodities as $commodity)
                                                    <li>{{ $commodity->name }} ({{ $commodity->pivot->quantity }})</li>
                                                @endforeach
                                            </ul>
                                        </td>
                                        <td>{{ $history->borrow_date }}</td>
                                        <td>{{ $history->return_date }}</td>
                                        @php $hs = $history->status ?? 'pending'; @endphp
                                        <td><span class="badge {{ $hs == 'approved' ? 'badge-success' : ($hs == 'pending' ? 'badge-warning' : 'badge-danger') }}">{{ ucfirst($hs) }}</span></td>
                                        <td>{{ $history->notes ?? '-' }}</td>
                                    </tr>
                                    @endforeach
                                </tbody>
                            </table>
                        </div>
                    @else
                        <p class="text-muted">Belum ada riwayat peminjaman.</p>
                    @endif
                </div>
            </div>
        </div>
    </div>
</div>

@push('scripts')
<script>
    function updateClock() {
        const now = new Date();
        let h = String(now.getHours()).padStart(2, '0');
        let m = String(now.getMinutes()).padStart(2, '0');
        let s = String(now.getSeconds()).padStart(2, '0');

        document.getElementById("hours").textContent = h;
        document.getElementById("minutes").textContent = m;
        document.getElementById("seconds").textContent = s;
    }
    setInterval(updateClock, 1000);
    updateClock();
</script>
@endpush

@push('scripts')
<script>
    function checkStatus() { location.reload(); }

    setInterval(function() {
        @if(isset($pendingBorrowings) && $pendingBorrowings > 0)
            location.reload();
        @endif
    }, 30000);

    // Notification functionality
    document.addEventListener('DOMContentLoaded', function() {
        const notificationBell = document.getElementById('notification-bell');
        const notificationDropdown = document.getElementById('notification-dropdown');
        const notificationBadge = document.getElementById('notification-badge');

        // Toggle dropdown on bell click
        notificationBell.addEventListener('click', function(e) {
            e.stopPropagation();
            notificationDropdown.style.display = notificationDropdown.style.display === 'block' ? 'none' : 'block';
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!notificationBell.contains(e.target) && !notificationDropdown.contains(e.target)) {
                notificationDropdown.style.display = 'none';
            }
        });

        // Mark notification as read when clicked
        notificationDropdown.addEventListener('click', function(e) {
            if (e.target.tagName === 'LI') {
                const notificationId = e.target.getAttribute('data-id');
                if (notificationId) {
                    // Send AJAX request to mark as read
                    fetch(`/notifications/${notificationId}/mark-as-read`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content')
                        }
                    }).then(response => {
                        if (response.ok) {
                            // Remove the notification from the list
                            e.target.remove();
                            // Update badge count
                            const currentCount = parseInt(notificationBadge.textContent) - 1;
                            if (currentCount > 0) {
                                notificationBadge.textContent = currentCount;
                            } else {
                                notificationBadge.style.display = 'none';
                            }
                        }
                    }).catch(error => {
                        console.error('Error marking notification as read:', error);
                    });
                }
            }
        });
    });
</script>
@endpush
@endsection
