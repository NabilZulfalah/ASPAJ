@extends('layouts.app')

@section('title', 'Kembalikan Barang')

@section('content')
<style>
    .return-container {
        max-width: 600px;
        margin: 0 auto;
        padding: 20px;
    }

    .return-card {
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        padding: 30px;
    }

    .return-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: #2c3e50;
        margin-bottom: 20px;
        text-align: center;
    }

    .item-info {
        background: #f8f9fa;
        border-radius: 8px;
        padding: 15px;
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        gap: 15px;
    }

    .item-image {
        width: 60px;
        height: 60px;
        object-fit: cover;
        border-radius: 6px;
        background: #e9ecef;
    }

    .item-details h6 {
        margin: 0;
        font-size: 1.1rem;
        font-weight: 600;
        color: #2c3e50;
    }

    .item-details p {
        margin: 0;
        font-size: 0.9rem;
        color: #6c757d;
    }

    .form-group {
        margin-bottom: 20px;
    }

    .form-label {
        font-weight: 600;
        color: #495057;
        margin-bottom: 8px;
        display: block;
    }

    .form-control {
        width: 100%;
        padding: 12px;
        border: 1px solid #ddd;
        border-radius: 8px;
        font-size: 1rem;
    }

    .form-control:focus {
        border-color: #007bff;
        outline: none;
    }

    .btn-submit {
        background: linear-gradient(135deg, #28a745, #20c997);
        color: white;
        border: none;
        padding: 12px 24px;
        border-radius: 8px;
        font-weight: 600;
        cursor: pointer;
        width: 100%;
        transition: all 0.3s ease;
    }

    .btn-submit:hover {
        background: linear-gradient(135deg, #20c997, #17a2b8);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(40,167,69,0.3);
    }

    .back-link {
        display: block;
        text-align: center;
        margin-top: 20px;
        color: #007bff;
        text-decoration: none;
    }

    .back-link:hover {
        text-decoration: underline;
    }
</style>

<div class="return-container">
    <div class="return-card">
        <h1 class="return-title">Kembalikan Barang</h1>

        <div class="item-info">
            @if($item->commodity->photo)
                <img src="{{ asset('storage/' . $item->commodity->photo) }}" alt="{{ $item->commodity->name }}" class="item-image">
            @else
                <div class="item-image" style="display: flex; align-items: center; justify-content: center;">
                    <i class="fas fa-image" style="color: #6c757d; font-size: 24px;"></i>
                </div>
            @endif
            <div class="item-details">
                <h6>{{ $item->commodity->name }}</h6>
                <p>Kode: {{ $item->commodity->code }}</p>
                <p>Jumlah: {{ $item->quantity }}</p>
            </div>
        </div>

        <form action="{{ route('students.borrowings.return.item.process', $item->id) }}" method="POST" enctype="multipart/form-data">
            @csrf
            @method('PATCH')

            <div class="form-group">
                <label for="condition" class="form-label">Kondisi Barang</label>
                <textarea name="condition" id="condition" class="form-control @error('condition') is-invalid @enderror" rows="4" placeholder="Deskripsikan kondisi barang saat dikembalikan..." required>{{ old('condition') }}</textarea>
                @error('condition')
                    <div class="text-danger mt-2">{{ $message }}</div>
                @enderror
            </div>

            <div class="form-group">
                <label for="photo" class="form-label">Foto Bukti Pengembalian</label>
                <input type="file" name="photo" id="photo" class="form-control @error('photo') is-invalid @enderror" accept="image/*" required>
                <small class="text-muted">Upload foto barang yang dikembalikan sebagai bukti.</small>
                @error('photo')
                    <div class="text-danger mt-2">{{ $message }}</div>
                @enderror
            </div>

            <button type="submit" class="btn-submit">Kembalikan Barang</button>
        </form>

        <a href="{{ route('students.borrowings.show', $item->borrowing_id) }}" class="back-link">
            <i class="fas fa-arrow-left"></i> Kembali ke Detail Peminjaman
        </a>
    </div>
</div>
@endsection