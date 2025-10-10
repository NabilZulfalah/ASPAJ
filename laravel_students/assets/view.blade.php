@extends('layouts.app')

@section('title', 'Lihat Barang')

@section('content')
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

<style>
    /* Animasi fade in */
    @keyframes fadeInUp {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .fade-seq {
        opacity: 0;
        animation: fadeInUp 0.5s ease forwards;
    }

    /* Container */
    .container-custom {
        background: #fff;
        max-width: 1100px;
        margin: 40px auto;
        padding: 25px;
        border-radius: 12px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        overflow: hidden;
    }

    .container-custom h1 {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 20px;
        color: #333;
    }

    /* Filter dan Pencarian */
    .filter-container {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        margin-bottom: 20px;
        align-items: center;
    }

    .filter-container input, .filter-container select {
        padding: 8px 10px;
        border: 1px solid #ddd;
        border-radius: 6px;
        font-size: 13px;
    }

    .btn-search {
        display: inline-flex;
        align-items: center;
        background: linear-gradient(90deg, #007bff, #0056b3);
        color: #fff;
        padding: 8px 14px;
        border-radius: 6px;
        text-decoration: none;
        font-size: 13px;
        font-weight: 500;
        border: none;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .btn-search:hover {
        background-color: #218838;
    }

    .btn-search i {
        margin-right: 6px;
    }

    /* Table responsive */
    .table-responsive {
        width: 100%;
        overflow-x: auto;
        border-radius: 10px;
    }

    /* Tabel compact */
    .table-custom {
        width: 100%;
        border-collapse: collapse;
        background: #fff;
        border-radius: 8px;
        font-size: 13px;
    }

    .table-custom th,
    .table-custom td {
        padding: 6px 8px;
        vertical-align: middle;
        text-align: center;
        border-top: 1px solid #dee2e6;
    }

    .table-custom th {
        background: #f1f3f5;
        font-weight: 600;
        color: #495057;
    }

    .table-custom tr:nth-child(even) {
        background: #f9fbfc;
    }
</style>

<div class="container-custom">
    <h1 class="fade-seq" style="animation-delay: 0.1s;">Lihat Barang</h1>

    <!-- Filter -->
    <form id="filter-form" method="GET" action="{{ route('students.assets.view') }}" class="filter-container fade-seq" style="animation-delay: 0.2s;">
        <input type="text" name="search" value="{{ request('search') }}" placeholder="Cari nama / kode...">
        <select name="jurusan" onchange="this.form.submit()">
            <option value="">Semua Jurusan</option>
            @foreach($jurusans as $jur)
                <option value="{{ $jur }}" {{ request('jurusan') == $jur ? 'selected' : '' }}>{{ $jur }}</option>
            @endforeach
        </select>
        <button type="submit" class="btn-search"><i class="fas fa-search"></i> Cari</button>
    </form>

    <!-- Table -->
    <div class="table-responsive fade-seq" style="animation-delay: 0.3s;">
        <table class="table-custom">
            <thead>
                <tr>
                    <th>Foto</th>
                    <th>Nama Barang</th>
                    <th>Kode</th>
                    <th>Jumlah</th>
                    <th>Lokasi</th>
                    <th>Jurusan</th>
                    <th>Merk</th>
                    <th>Sumber</th>
                    <th>Tahun</th>
                    <th>Deskripsi</th>
                </tr>
            </thead>
            <tbody>
                @forelse($assets as $asset)
                    <tr>
                        <td>
                            @if($asset->photo)
                                <img src="{{ $asset->photo }}" alt="{{ $asset->name }}" width="50" style="border-radius: 5px;">
                            @else
                                <i class="fas fa-box" style="font-size: 24px; color: #ccc;"></i>
                            @endif
                        </td>
                        <td>{{ $asset->name }}</td>
                        <td>{{ $asset->code }}</td>
                        <td>{{ $asset->stock }}</td>
                        <td>{{ $asset->lokasi }}</td>
                        <td>{{ $asset->jurusan }}</td>
                        <td>{{ $asset->merk }}</td>
                        <td>{{ $asset->sumber }}</td>
                        <td>{{ $asset->tahun }}</td>
                        <td style="max-width:200px; white-space: normal;">{{ $asset->deskripsi }}</td>
                    </tr>
                @empty
                    <tr>
                        <td colspan="10">Tidak ada aset yang tersedia.</td>
                    </tr>
                @endforelse
            </tbody>
        </table>
    </div>
</div>
@endsection
