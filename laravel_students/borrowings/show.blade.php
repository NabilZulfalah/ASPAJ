@extends('layouts.app')

@section('title', 'Detail Peminjaman')

@section('content')
<style>
    .detail-container {
        max-width: 800px;
        margin: 0 auto;
        padding: 20px;
    }

    .detail-card {
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        padding: 30px;
        margin-bottom: 20px;
    }

    .detail-header {
        border-bottom: 2px solid #f8f9fa;
        padding-bottom: 20px;
        margin-bottom: 20px;
    }

    .detail-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: #2c3e50;
        margin-bottom: 10px;
    }

    .status-badge {
        display: inline-block;
        padding: 8px 16px;
        border-radius: 20px;
        font-weight: 600;
        font-size: 0.9rem;
        text-transform: uppercase;
    }

    .status-pending { background: #ffc107; color: #000; }
    .status-approved { background: #28a745; color: #fff; }
    .status-rejected { background: #dc3545; color: #fff; }
    .status-returned { background: #17a2b8; color: #fff; }

    .detail-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f8f9fa;
    }

    .detail-row:last-child {
        border-bottom: none;
    }

    .detail-label {
        font-weight: 600;
        color: #495057;
        min-width: 150px;
    }

    .detail-value {
        color: #6c757d;
        flex: 1;
        text-align: right;
    }

    .items-section {
        margin-top: 30px;
    }

    .items-title {
        font-size: 1.2rem;
        font-weight: 600;
        color: #2c3e50;
        margin-bottom: 15px;
    }

    .item-card {
        background: #f8f9fa;
        border-radius: 8px;
        padding: 15px;
        margin-bottom: 10px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .item-info {
        display: flex;
        align-items: center;
        gap: 15px;
    }

    .item-image {
        width: 50px;
        height: 50px;
        object-fit: cover;
        border-radius: 6px;
        background: #e9ecef;
    }

    .item-details h6 {
        margin: 0;
        font-size: 1rem;
        font-weight: 600;
        color: #2c3e50;
    }

    .item-details p {
        margin: 0;
        font-size: 0.9rem;
        color: #6c757d;
    }

    .item-quantity {
        font-weight: 600;
        color: #007bff;
    }

    .back-btn {
        background: linear-gradient(135deg, #007bff, #0056b3);
        color: white;
        border: none;
        padding: 12px 24px;
        border-radius: 8px;
        font-weight: 600;
        text-decoration: none;
        display: inline-block;
        transition: all 0.3s ease;
    }

    .back-btn:hover {
        background: linear-gradient(135deg, #0056b3, #004085);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0,123,255,0.3);
        color: white;
        text-decoration: none;
    }

    .return-info {
        background: #e7f3ff;
        border: 1px solid #b3d9ff;
        border-radius: 8px;
        padding: 15px;
        margin-top: 20px;
    }

    .return-info h6 {
        color: #0056b3;
        margin-bottom: 10px;
    }
</style>

<div class="detail-container">
    <div class="detail-card">
        <div class="detail-header">
            <h1 class="detail-title">Detail Peminjaman</h1>
        </div>

        <div class="detail-row">
            <span class="detail-label">ID Peminjaman:</span>
            <span class="detail-value">#{{ $borrowing->id }}</span>
        </div>

        <div class="detail-row">
            <span class="detail-label">Tanggal Pinjam:</span>
            <span class="detail-value">{{ $borrowing->borrow_date }}</span>
        </div>

        <div class="detail-row">
            <span class="detail-label">Tanggal Kembali:</span>
            <span class="detail-value">{{ $borrowing->return_date }}</span>
        </div>

        <div class="detail-row">
            <span class="detail-label">Tujuan:</span>
            <span class="detail-value">{{ $borrowing->tujuan ?? '-' }}</span>
        </div>

        <div class="detail-row">
            <span class="detail-label">Tanggal Dibuat:</span>
            <span class="detail-value">{{ $borrowing->created_at->format('d M Y H:i') }}</span>
        </div>

        @if($borrowing->status === 'returned')
            <div class="detail-row">
                <span class="detail-label">Tanggal Dikembalikan:</span>
                <span class="detail-value">{{ $borrowing->return_date_actual ?? $borrowing->updated_at->format('d M Y H:i') }}</span>
            </div>

            <div class="detail-row">
                <span class="detail-label">Kondisi Pengembalian:</span>
                <span class="detail-value">{{ $borrowing->return_condition ?? '-' }}</span>
            </div>
        @endif

        <div class="items-section">
            <h3 class="items-title">Barang yang Dipinjam</h3>
            @foreach($borrowing->items as $item)
                <div class="item-card">
                    <div class="item-info">
                        @if($item->commodity->photo)
                            <img src="{{ asset('storage/' . $item->commodity->photo) }}" alt="{{ $item->commodity->name }}" class="item-image">
                        @else
                            <div class="item-image" style="display: flex; align-items: center; justify-content: center; background: #e9ecef;">
                                <i class="fas fa-image" style="color: #6c757d;"></i>
                            </div>
                        @endif
                        <div class="item-details">
                            <h6>{{ $item->commodity->name }}</h6>
                            <p>Kode: {{ $item->commodity->code }}</p>
                            <span class="status-badge status-{{ $item->status }}">
                                {{ ucfirst($item->status) }}
                            </span>
                            @if($item->status === 'approved')
                                @php
                                    $location = $item->commodity->lokasi ?? ('Gudang ' . $item->commodity->jurusan);
                                @endphp
                                <p><strong>Lokasi:</strong> {{ $location }}</p>
                                <div style="background: #d4edda; border: 1px solid #c3e6cb; border-radius: 6px; padding: 8px; margin-top: 5px; font-size: 0.85rem; color: #155724;">
                                    <i class="fas fa-info-circle"></i> Silakan ambil barang di {{ $location }} segera.
                                </div>
                                <div style="margin-top: 10px;">
                                    <a href="{{ route('students.borrowings.return.item', $item->id) }}" class="btn btn-warning btn-sm">Kembalikan Barang</a>
                                </div>
                            @endif
                        </div>
                    </div>
                    <div class="item-quantity">
                        Qty: {{ $item->quantity }}
                    </div>
                </div>
            @endforeach
        </div>

        @php
            $returnedItemsWithPhoto = $borrowing->items->where('status', 'returned')->whereNotNull('return_photo');
        @endphp
        @if($borrowing->status === 'returned' && $returnedItemsWithPhoto->count() > 0)
            <div class="return-info">
                <h6><i class="fas fa-camera"></i> Foto Pengembalian</h6>
                @foreach($returnedItemsWithPhoto as $item)
                    <p><strong>{{ $item->commodity->name }}:</strong></p>
                    <img src="{{ asset('storage/' . $item->return_photo) }}" alt="Return Photo for {{ $item->commodity->name }}" style="max-width: 100%; border-radius: 8px; margin-bottom: 10px;">
                @endforeach
            </div>
        @endif
    </div>

    <div style="text-align: center;">
        <a href="{{ route('students.borrowings.index') }}" class="back-btn">
            <i class="fas fa-arrow-left"></i> Kembali ke Daftar Peminjaman
        </a>
    </div>
</div>
@endsection
