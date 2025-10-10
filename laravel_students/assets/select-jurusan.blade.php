@extends('layouts.app')

@section('title', 'Pilih Jurusan')

@section('content')
<style>
    /* Animasi muncul dari bawah */
    @keyframes slideUp {
        from {
            opacity: 0;
            transform: translateY(40px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
    .slide-up {
        opacity: 0;
        animation: slideUp 0.6s ease forwards;
    }

    /* Hover effect */
    .jurusan-card {
        transition: transform 0.25s ease, box-shadow 0.25s ease;
    }
    .jurusan-card:hover {
        transform: translateY(-5px) scale(1.03);
        box-shadow: 0 6px 14px rgba(0,0,0,0.15);
    }
</style>

<div class="container-custom slide-up" style="max-width: 600px; margin: 40px auto; padding: 25px; background: #fff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05);">
    <h1 class="slide-up" style="font-size: 24px; font-weight: 600; margin-bottom: 20px; color: #333; animation-delay: 0.1s;">
        Pilih Jurusan
    </h1>

    <div style="display: flex; flex-wrap: wrap; gap: 15px;">
        @foreach($jurusanList as $jurusan)
            @php
                $colors = [
                    'Rekayasa Perangkat Lunak' => '#28a745', // green
                    'Desain Komunikasi Visual' => '#6f42c1', // purple
                    'Teknik Otomasi Industri' => '#6c757d', // gray
                    'Teknik Instalasi Tenaga Listrik' => '#0d6efd', // dark blue
                    'Teknik Audio Video' => '#ffc107', // yellow
                    'Teknik Komputer Jaringan' => '#dc3545', // red
                ];
                $icons = [
                    'Rekayasa Perangkat Lunak' => 'fa-laptop-code',
                    'Desain Komunikasi Visual' => 'fa-palette',
                    'Teknik Otomasi Industri' => 'fa-cogs',
                    'Teknik Instalasi Tenaga Listrik' => 'fa-bolt',
                    'Teknik Audio Video' => 'fa-video',
                    'Teknik Komputer Jaringan' => 'fa-network-wired',
                ];
                $color = $colors[$jurusan] ?? '#007bff';
                $icon = $icons[$jurusan] ?? 'fa-book';
            @endphp
            <a href="{{ route('students.assets.index', ['jurusan' => $jurusan]) }}" 
               class="slide-up jurusan-card"
               style="flex: 1 1 45%; padding: 15px; background: {{ $color }}; color: white; text-align: center; border-radius: 8px; text-decoration: none; font-weight: 600; box-shadow: 0 2px 6px rgba(0,0,0,0.1); display: flex; align-items: center; justify-content: center; gap: 8px; animation-delay: {{ $loop->index * 0.15 + 0.2 }}s;">
                <i class="fas {{ $icon }}"></i> {{ $jurusan }}
            </a>
        @endforeach
    </div>
</div>
@endsection
