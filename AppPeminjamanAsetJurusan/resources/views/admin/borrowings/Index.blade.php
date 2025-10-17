@extends('layouts.app')

@section('title', 'Riwayat Peminjaman')

@section('content')
<style>
    body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        color: #2c3e50;
    }

    .container {
        max-width: 1200px;
        margin: auto;
        padding: 30px;
        animation: fadeIn 1s ease-in-out;
    }

    h1 {
        font-weight: bold;
        color: #34495e;
        text-align: center;
        margin-bottom: 30px;
        animation: slideDown 0.8s ease-in-out;
    }

    .alert {
        padding: 15px 20px;
        background-color: #dff0d8;
        color: #3c763d;
        border-radius: 8px;
        margin-bottom: 20px;
        animation: fadeIn 0.5s ease-in-out;
    }

    /* Filters */
    form.filters {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        margin-bottom: 25px;
        justify-content: center;
        align-items: center;
    }
    form.filters input[type="text"],
    form.filters select {
        padding: 10px 14px;
        border: 1px solid #ddd;
        border-radius: 8px;
        font-size: 14px;
        min-width: 180px;
        transition: border-color 0.3s ease;
    }
    form.filters input[type="text"]:focus,
    form.filters select:focus {
        border-color: #007bff;
        outline: none;
    }
    form.filters button {
        background: linear-gradient(90deg, #007bff, #0056b3);
        color: #fff;
        padding: 10px 18px;
        border-radius: 8px;
        font-size: 14px;
        font-weight: 600;
        border: none;
        cursor: pointer;
        transition: background-color 0.3s ease;
        display: flex;
        align-items: center;
        gap: 6px;
    }
    form.filters button:hover {
        background: linear-gradient(90deg, #0056b3, #003d7a);
    }
    form.filters button i {
        font-size: 16px;
    }

    /* Card container */
    .cards-container {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
        gap: 20px;
    }

    /* Individual card */
    .borrowing-card {
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        padding: 20px;
        display: flex;
        flex-direction: column;
        gap: 14px;
        transition: box-shadow 0.3s ease;
    }
    .borrowing-card:hover {
        box-shadow: 0 6px 18px rgba(0,0,0,0.12);
    }

    /* Header section with profile */
    .card-header {
        display: flex;
        align-items: center;
        gap: 15px;
    }
    .profile-photo {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        object-fit: cover;
        border: 2px solid #007bff;
    }
    .student-info {
        display: flex;
        flex-direction: column;
        font-weight: 600;
        color: #34495e;
        font-size: 16px;
    }
    .student-class {
        font-weight: 400;
        font-size: 14px;
        color: #7f8c8d;
    }

    /* Items list */
    .items-list {
        list-style: none;
        padding: 0;
        margin: 0;
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
    }
    .items-list li {
        display: flex;
        align-items: center;
        gap: 10px;
        background: #f7f9fc;
        padding: 8px 12px;
        border-radius: 8px;
        box-shadow: inset 0 0 5px rgba(0,0,0,0.03);
        font-size: 14px;
        color: #2c3e50;
        flex: 1 1 45%;
        min-width: 140px;
    }
    .items-list img, .items-list i {
        width: 36px;
        height: 36px;
        border-radius: 6px;
        object-fit: cover;
        color: #ccc;
        flex-shrink: 0;
    }

    /* Details grid */
    .details-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
        gap: 12px 20px;
        font-size: 14px;
        color: #555;
    }
    .details-grid div {
        background: #f9f9f9;
        padding: 10px 14px;
        border-radius: 8px;
        box-shadow: inset 0 0 5px rgba(0,0,0,0.02);
    }
    .details-label {
        font-weight: 600;
        color: #34495e;
        margin-bottom: 4px;
        display: block;
    }

    /* Status badge */
    .status {
        font-weight: 700;
        border-radius: 12px;
        padding: 6px 14px;
        display: inline-block;
        color: #fff;
        font-size: 13px;
        text-transform: capitalize;
        user-select: none;
    }
    .status.pending { background-color: #f39c12; }
    .status.approved { background-color: #3498db; }
    .status.rejected { background-color: #e74c3c; }
    .status.returned { background-color: #2ecc71; }
    .status.partial { background-color: #f39c12; }
    .status.partially_approved { background-color: #9b59b6; }
    .status.partially_returned { background-color: #27ae60; }

    /* Action buttons */
    .actions {
        display: flex;
        gap: 10px;
        flex-wrap: wrap;
        margin-top: 10px;
    }
    .actions form, .actions span {
        margin: 0;
    }
    .actions button, .actions span {
        background: #007bff;
        color: #fff;
        border: none;
        padding: 8px 14px;
        border-radius: 8px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: background-color 0.3s ease;
        user-select: none;
        display: inline-flex;
        align-items: center;
        gap: 6px;
    }
    .actions button:hover {
        background: #0056b3;
    }
    .actions span {
        background: #2ecc71;
        cursor: default;
    }
    .actions .rejected {
        background: #e74c3c;
        cursor: default;
    }

    /* Modal styles */
    .modal {
        display: none;
        position: fixed;
        z-index: 1000;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0,0,0,0.8);
    }

    .modal-content {
        background-color: #fefefe;
        margin: 5% auto;
        padding: 20px;
        border: 1px solid #888;
        width: 80%;
        max-width: 600px;
        border-radius: 12px;
        box-shadow: 0 8px 24px rgba(0,0,0,0.2);
        animation: fadeIn 0.3s ease-in-out;
    }

    .close {
        color: #aaa;
        float: right;
        font-size: 28px;
        font-weight: bold;
        cursor: pointer;
        user-select: none;
    }

    .close:hover { color: black; }

    .modal img {
        width: 100%;
        height: auto;
        border-radius: 8px;
    }

    /* Pagination */
    .pagination-container {
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
        margin-top: 25px;
        gap: 10px;
    }

    .entries-info {
        font-size: 14px;
        color: #555;
    }

    .pagination {
        list-style: none;
        display: flex;
        gap: 6px;
        padding: 0;
        margin: 0;
    }

    .pagination li { display: inline-block; }

    .pagination a,
    .pagination span {
        padding: 6px 12px;
        border: 1px solid #ddd;
        border-radius: 8px;
        font-size: 14px;
        color: #333;
        text-decoration: none;
        transition: all 0.2s;
        user-select: none;
    }

    .pagination a:hover {
        background-color: #007bff;
        color: #fff;
        border-color: #007bff;
    }

    .pagination .active span {
        background-color: #007bff;
        color: #fff;
        border-color: #007bff;
    }

    .pagination .disabled span {
        color: #aaa;
        background: #f9f9f9;
        cursor: not-allowed;
    }

    @keyframes fadeIn {
        from { opacity: 0; }
        to { opacity: 1; }
    }

    @keyframes slideDown {
        from { opacity: 0; transform: translateY(-20px); }
        to { opacity: 1; transform: translateY(0); }
    }
</style>

<!-- Modal for viewing photos -->
<div id="photoModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h3>Foto Bukti Pengembalian</h3>
        <div id="photoContainer"></div>
        <p id="noPhotoMsg" style="text-align: center; color: #666; display: none;">Tidak ada foto tersedia</p>
    </div>
</div>

<!-- Modal for approving items -->
<div id="approveModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeApproveModal()">&times;</span>
        <h3>Pilih Barang untuk Disetujui</h3>
        <form id="approveForm" method="POST">
            @csrf
            <div id="approveItemsList"></div>
            <button type="submit">Setujui Terpilih</button>
        </form>
    </div>
</div>

<!-- Modal for rejecting items -->
<div id="rejectModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeRejectModal()">&times;</span>
        <h3>Pilih Barang untuk Ditolak</h3>
        <form id="rejectForm" method="POST">
            @csrf
            <div id="rejectItemsList"></div>
            <button type="submit" class="rejected">Tolak Terpilih</button>
        </form>
    </div>
</div>

<script>
    function viewPhotos(photoUrls) {
        var container = document.getElementById('photoContainer');
        var msg = document.getElementById('noPhotoMsg');
        container.innerHTML = '';
        if (photoUrls && photoUrls.length > 0) {
            msg.style.display = 'none';
            photoUrls.forEach(function(url) {
                var img = document.createElement('img');
                img.src = url;
                img.style.width = '100%';
                img.style.height = 'auto';
                img.style.borderRadius = '8px';
                img.style.marginBottom = '10px';
                container.appendChild(img);
            });
        } else {
            msg.style.display = 'block';
        }
        document.getElementById('photoModal').style.display = 'block';
    }

    function closeModal() {
        document.getElementById('photoModal').style.display = 'none';
    }

    function openApproveModal(borrowingId) {
        var itemsHtml = '';
        @foreach($borrowings as $borrowing)
            if ({{ $borrowing->id }} === borrowingId) {
                @foreach($borrowing->items as $item)
                    @if($item->status == 'pending')
                        itemsHtml += '<label><input type="checkbox" name="items[]" value="{{ $item->id }}"> {{ $item->commodity->name }} ({{ $item->quantity }} unit)</label><br>';
                    @endif
                @endforeach
            }
        @endforeach
        document.getElementById('approveItemsList').innerHTML = itemsHtml;
        document.getElementById('approveForm').action = '{{ route("borrowings.approve.admin", ":id") }}'.replace(':id', borrowingId);
        document.getElementById('approveModal').style.display = 'block';
    }

    function closeApproveModal() {
        document.getElementById('approveModal').style.display = 'none';
    }

    function openRejectModal(borrowingId) {
        var itemsHtml = '';
        @foreach($borrowings as $borrowing)
            if ({{ $borrowing->id }} === borrowingId) {
                @foreach($borrowing->items as $item)
                    @if($item->status == 'pending')
                        itemsHtml += '<label><input type="checkbox" name="items[]" value="{{ $item->id }}"> {{ $item->commodity->name }} ({{ $item->quantity }} unit)</label><br>';
                    @endif
                @endforeach
            }
        @endforeach
        document.getElementById('rejectItemsList').innerHTML = itemsHtml;
        document.getElementById('rejectForm').action = '{{ route("borrowings.reject.admin", ":id") }}'.replace(':id', borrowingId);
        document.getElementById('rejectModal').style.display = 'block';
    }

    function closeRejectModal() {
        document.getElementById('rejectModal').style.display = 'none';
    }

    window.onclick = function(event) {
        var photoModal = document.getElementById('photoModal');
        var approveModal = document.getElementById('approveModal');
        var rejectModal = document.getElementById('rejectModal');
        if (event.target == photoModal) {
            photoModal.style.display = 'none';
        }
        if (event.target == approveModal) {
            approveModal.style.display = 'none';
        }
        if (event.target == rejectModal) {
            rejectModal.style.display = 'none';
        }
    }
</script>

<div class="container">
    <h1>Kelola Peminjaman</h1>

    @if (session('success'))
        <div class="alert alert-success">
            {{ session('success') }}
        </div>
    @endif

    <!-- Search & Filter -->
    <form method="GET" action="{{ route('admin.borrowings.index') }}" class="filters">
        <input type="text" name="search" value="{{ request('search') }}" placeholder="Cari nama siswa / barang...">
        <select name="status" onchange="this.form.submit()">
            <option value="">Semua Status</option>
            @foreach($statusList as $status)
                <option value="{{ $status }}" {{ request('status') == $status ? 'selected' : '' }}>
                    {{ ucfirst($status) }}
                </option>
            @endforeach
        </select>
        <select name="jurusan" onchange="this.form.submit()">
            <option value="">Semua Jurusan</option>
            @foreach($jurusanList as $jurusan)
                <option value="{{ $jurusan }}" {{ request('jurusan') == $jurusan ? 'selected' : '' }}>
                    {{ $jurusan }}
                </option>
            @endforeach
        </select>
        <select name="class" onchange="this.form.submit()">
            <option value="">Semua Kelas</option>
            @foreach($classList as $class)
                <option value="{{ $class }}" {{ request('class') == $class ? 'selected' : '' }}>
                    {{ $class }}
                </option>
            @endforeach
        </select>
        <button type="submit"><i class="fas fa-search"></i> Cari</button>
    </form>

    <div class="cards-container">
        @forelse ($borrowings as $borrowing)
            <div class="borrowing-card">
                <div class="card-header">
                    @if($borrowing->student && $borrowing->student->user)
                        <img src="{{ $borrowing->student->user->profile_picture_url }}" alt="Foto Profile" class="profile-photo">
                    @else
                        <img src="{{ asset('ASSETS/4ALL.png') }}" alt="Foto Profile" class="profile-photo">
                    @endif
                    <div class="student-info">
                        <span>{{ $borrowing->student->name ?? '-' }}</span>
                        <span class="student-class">{{ $borrowing->student->schoolClass->name ?? '-' }}</span>
                    </div>
                </div>

                <ul class="items-list">
                    @foreach($borrowing->items as $item)
                        <li>
                            @if($item->commodity->photo)
                                <img src="{{ $item->commodity->photo }}" alt="{{ $item->commodity->name }}">
                            @else
                                <i class="fas fa-box"></i>
                            @endif
                            <span>{{ $item->commodity->name }} ({{ $item->quantity }} unit) - <small class="status {{ $item->status }}">{{ ucfirst($item->status) }}</small></span>
                        </li>
                    @endforeach
                </ul>

                <div class="details-grid">
                    <div>
                        <span class="details-label">Tujuan</span>
                        <span>{{ $borrowing->tujuan ?? '-' }}</span>
                    </div>
                    <div>
                        <span class="details-label">Tanggal / Jam</span>
                        <span>{{ $borrowing->borrow_date ? \Carbon\Carbon::parse($borrowing->borrow_date)->format('d M Y - H:i') : '-' }}</span>
                    </div>
                    <div>
                        <span class="details-label">Status</span>
                        <span class="status {{ $borrowing->status }}">
                            {{ ucfirst($borrowing->status) }}
                        </span>
                    </div>
                    <div>
                        <span class="details-label">Kondisi Pengembalian</span>
                        <span>{{ $borrowing->return_condition ?: '-' }}</span>
                    </div>
                    <div>
                        <span class="details-label">Dikembalikan Oleh</span>
                        <span>{{ $borrowing->student->name ?? '-' }}</span>
                    </div>
                    <div>
                        <span class="details-label">Foto Pengembalian</span>
                        @if($borrowing->status === 'returned' && $borrowing->return_photo)
                            <button type="button" class="btn btn-info btn-sm" onclick="viewPhotos(['{{ asset('storage/' . $borrowing->return_photo) }}'])">Lihat</button>
                        @else
                            <span>-</span>
                        @endif
                    </div>
                </div>

                <div class="actions">
                    @if ($borrowing->items->where('status', 'pending')->count() > 0)
                        <button type="button" onclick="openApproveModal({{ $borrowing->id }})">Setujui</button>
                        <button type="button" onclick="openRejectModal({{ $borrowing->id }})" class="rejected">Tolak</button>
                    @elseif ($borrowing->status === 'approved' || $borrowing->status === 'partially_approved')
                        <form action="{{ route('borrowings.return.admin', $borrowing->id) }}" method="POST" style="display:inline">
                            @csrf
                            @method('PATCH')
                            <button type="submit">Kembalikan</button>
                        </form>
                    @elseif ($borrowing->status === 'returned' || $borrowing->status === 'partially_returned')
                        <span>Sudah Dikembalikan</span>
                    @elseif ($borrowing->status === 'rejected')
                        <span class="rejected">Ditolak</span>
                    @elseif ($borrowing->status === 'partial')
                        <span class="rejected">Status Campuran</span>
                    @endif
                </div>
            </div>
        @empty
            <p>Belum ada data peminjaman.</p>
        @endforelse
    </div>

    <!-- Pagination -->
    <div class="pagination-container">
        <div class="entries-info">
            Showing {{ $borrowings->firstItem() }} to {{ $borrowings->lastItem() }} of {{ $borrowings->total() }} entries
        </div>
        <ul class="pagination">
            @if ($borrowings->onFirstPage())
                <li class="disabled"><span>Previous</span></li>
            @else
                <li><a href="{{ $borrowings->previousPageUrl() }}">Previous</a></li>
            @endif

            @foreach ($borrowings->onEachSide(1)->links()->elements as $element)
                @if (is_string($element))
                    <li class="disabled"><span>{{ $element }}</span></li>
                @endif

                @if (is_array($element))
                    @foreach ($element as $page => $url)
                        @if ($page == $borrowings->currentPage())
                            <li class="active"><span>{{ $page }}</span></li>
                        @else
                            <li><a href="{{ $url }}">{{ $page }}</a></li>
                        @endif
                    @endforeach
                @endif
            @endforeach

            @if ($borrowings->hasMorePages())
                <li><a href="{{ $borrowings->nextPageUrl() }}">Next</a></li>
            @else
                <li class="disabled"><span>Next</span></li>
            @endif
        </ul>
    </div>

    <div style="margin-top: 20px; text-align: center;">
        {{ $borrowings->links() }}
    </div>
</div>

@if(session('error'))
<script>
Swal.fire({
  icon: 'error',
  title: 'Error',
  text: '{{ session('error') }}',
  confirmButtonText: 'OK'
});
</script>
@endif

@endsection

@php
$stockEmptyNotification = $notifications->first(function($n) { return ($n->data['type'] ?? null) === 'stock_empty' && !$n->read_at; });
@endphp
@if($stockEmptyNotification)
<script>
Swal.fire({
  title: 'Stok Kosong',
  text: '{{ $stockEmptyNotification->data['message'] }}',
  icon: 'warning',
  confirmButtonText: 'OK'
}).then(() => {
  fetch('/notifications/{{ $stockEmptyNotification->id }}/mark-as-read', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-CSRF-TOKEN': '{{ csrf_token() }}'
    }
  });
});
</script>
@endif
