@extends('layouts.app')

@section('title', 'Status Peminjaman Saya')

@section('content')
<style>
    .page-container {
        width: 100%;
        padding: 20px 30px;
        display: flex;
        flex-direction: column;
        gap: 20px;
    }

    .page-title {
        font-size: 1.8rem;
        font-weight: 700;
        color: #2c3e50;
        text-align: center;
        margin-bottom: 25px;
        animation: fadeDown 0.8s ease forwards;
    }

    .cards-container {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 20px;
        justify-items: center;
    }

    .card {
        background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
        border-radius: 16px;
        box-shadow: 0 8px 32px rgba(0,0,0,0.1), 0 2px 8px rgba(0,0,0,0.05);
        padding: 24px;
        width: 320px;
        opacity: 0;
        transform: translateY(30px);
        animation: fadeUp 0.8s ease forwards;
        display: flex;
        flex-direction: column;
        gap: 16px;
        transition: transform 0.3s ease, box-shadow 0.3s ease;
        border: 1px solid rgba(255,255,255,0.2);
    }

    .card:hover {
        transform: translateY(-5px);
        box-shadow: 0 12px 40px rgba(0,0,0,0.15), 0 4px 12px rgba(0,0,0,0.1);
    }

    .card img, .card .no-image {
        width: 100%;
        height: 150px;
        object-fit: cover;
        border-radius: 8px;
        background-color: #eaeaea;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 3rem;
        color: #ccc;
    }

    .slider {
        position: relative;
        width: 100%;
        height: 150px;
        overflow: hidden;
        border-radius: 8px;
    }

    .slider-images {
        display: flex;
        width: 100%;
        height: 100%;
        transition: transform 0.5s ease;
    }

    .slider img, .slider .no-image {
        min-width: 100%;
        height: 100%;
        object-fit: cover;
        flex-shrink: 0;
    }

    .slider-controls {
        position: absolute;
        top: 50%;
        left: 0;
        right: 0;
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
        margin-top: 0;
        transform: translateY(-50%);
        pointer-events: none;
    }

    .slider-btn {
        position: absolute;
        top: 50%;
        transform: translateY(-50%);
        background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
        color: white;
        border: none;
        padding: 6px 8px;
        border-radius: 50%;
        cursor: pointer;
        font-size: 0.8rem;
        transition: background 0.3s ease, transform 0.2s ease, opacity 0.3s ease;
        box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        pointer-events: all;
        z-index: 10;
        opacity: 0.8;
    }

    .slider-btn.prev {
        left: 10px;
    }

    .slider-btn.next {
        right: 10px;
    }

    .slider-btn:hover {
        background: linear-gradient(135deg, #0056b3 0%, #004085 100%);
        transform: translateY(-50%) scale(1.1);
        opacity: 1;
    }

    .dots {
        display: flex;
        gap: 8px;
        justify-content: center;
        margin-top: 12px;
    }

    .dot {
        width: 12px;
        height: 12px;
        background-color: #bbb;
        border-radius: 50%;
        display: inline-block;
        cursor: pointer;
        transition: background-color 0.3s ease;
        box-shadow: 0 0 3px rgba(0,0,0,0.1);
    }

    .dot.active {
        background-color: #007bff;
        box-shadow: 0 0 8px #007bff;
    }

    .current-commodity {
        font-weight: 700;
        font-size: 1.1rem;
        color: #333;
        margin-top: 8px;
        text-align: center;
    }

    .info-text {
        font-size: 0.9rem;
        margin-bottom: 4px;
        display: flex;
        align-items: center;
        gap: 8px;
    }

    .info-text b {
        font-weight: 700;
    }



    .status-ongoing, .status-returned, .btn-primary {
        border-radius: 12px;
        padding: 8px 16px;
        font-weight: 700;
        font-size: 0.9rem;
        text-align: center;
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        transition: background-color 0.3s ease, box-shadow 0.3s ease;
        cursor: default;
        user-select: none;
        max-width: fit-content;
        margin-top: 12px;
        display: flex;
        align-items: center;
        gap: 8px;
    }

    .status-ongoing {
        background: #ffc107;
        color: #000;
    }

    .status-ongoing:hover {
        background: #e0a800;
        box-shadow: 0 6px 16px rgba(255,193,7,0.4);
    }

    .status-ongoing::before {
        content: '\f00c'; /* fa-check */
        font-family: 'Font Awesome 5 Free';
        font-weight: 900;
    }

    .status-returned {
        background: #28a745;
        color: white;
    }

    .status-returned:hover {
        background: #218838;
        box-shadow: 0 6px 16px rgba(40,167,69,0.4);
    }

    .status-returned::before {
        content: '\f2f1'; /* fa-sync */
        font-family: 'Font Awesome 5 Free';
        font-weight: 900;
    }

    .btn-primary {
        background: #007bff;
        color: white;
        border: none;
        cursor: pointer;
        user-select: auto;
        max-width: fit-content;
        margin-top: 12px;
        align-self: flex-start;
        box-shadow: 0 6px 16px rgba(0,123,255,0.4);
        transition: background-color 0.3s ease, box-shadow 0.3s ease;
    }

    .btn-primary:hover {
        background: #0056b3;
        box-shadow: 0 8px 20px rgba(0,86,179,0.6);
    }

    .btn-primary::before {
        content: '\f0e2'; /* fa-arrow-left */
        font-family: 'Font Awesome 5 Free';
        font-weight: 900;
    }

    .text-muted {
        font-size: 0.9rem;
        color: #777;
    }

    .status-rejected {
        font-size: 0.9rem;
        color: #dc3545;
        font-weight: 700;
    }

    /* Animations */
    @keyframes fadeUp {
        from { opacity: 0; transform: translateY(30px); }
        to   { opacity: 1; transform: translateY(0); }
    }
    @keyframes fadeDown {
        from { opacity: 0; transform: translateY(-20px); }
        to   { opacity: 1; transform: translateY(0); }
    }

    /* Modal z-index fix */
    .modal {
        z-index: 1200;
    }
</style>

<div class="page-container">
    <h1 class="page-title">Status Peminjaman Saya</h1>

    <div class="cards-container">
        @forelse ($borrowings as $borrowing)
            <div class="card" id="card-{{ $borrowing->id }}" data-commodities='{{ json_encode($borrowing->commodities->map(fn($c) => ["name" => $c->name, "quantity" => $c->pivot->quantity, "photo" => $c->photo])) }}' data-items='{{ json_encode($borrowing->items->where("status", "approved")->map(fn($item) => ["id" => $item->id, "name" => $item->commodity->name])) }}'>
                @if($borrowing->commodities->count() > 1)
                    <div class="slider" id="slider-{{ $borrowing->id }}">
                        <div class="slider-images" style="transform: translateX(0%);">
                            @foreach($borrowing->commodities as $index => $commodity)
                                @if($commodity->photo)
                                    <img src="{{ $commodity->photo }}" alt="{{ $commodity->name }}">
                                @else
                                    <div class="no-image"><i class="fas fa-camera"></i></div>
                                @endif
                            @endforeach
                        </div>
                        <div class="slider-controls">
                            <button class="slider-btn prev" onclick="prevCommodity('{{ $borrowing->id }}')"><i class="fas fa-arrow-left"></i></button>
                            <button class="slider-btn next" onclick="nextCommodity('{{ $borrowing->id }}')"><i class="fas fa-arrow-right"></i></button>
                        </div>
                    </div>
                    <div class="dots" id="dots-{{ $borrowing->id }}">
                        @foreach($borrowing->commodities as $index => $commodity)
                            <span class="dot {{ $index == 0 ? 'active' : '' }}" onclick="goToCommodity('{{ $borrowing->id }}', {{ $index }})"></span>
                        @endforeach
                    </div>
                    <div class="current-commodity" id="commodity-info-{{ $borrowing->id }}">
                        @php $first = $borrowing->commodities->first(); @endphp
                        <div class="commodity-name">{{ $first->name }} ({{ $first->pivot->quantity }} unit)</div>
                    </div>
                @else
                    @php
                        $firstCommodity = $borrowing->commodities->first();
                    @endphp
                    @if($firstCommodity && $firstCommodity->photo)
                        <img src="{{ $firstCommodity->photo }}" alt="{{ $firstCommodity->name }}">
                    @else
                        <div class="no-image"><i class="fas fa-camera"></i></div>
                    @endif
                    <div class="current-commodity">
                        <div class="commodity-name">{{ $firstCommodity->name }} ({{ $firstCommodity->pivot->quantity }} unit)</div>
                    </div>
                @endif

                <div class="info-text"><i class="fas fa-file-alt"></i> <b>Tujuan:</b> {{ $borrowing->tujuan ?? '-' }}</div>
                <div class="info-text"><i class="fas fa-calendar"></i> <b>Tgl Pinjam:</b> {{ $borrowing->borrow_date }}</div>
                <div class="info-text"><i class="fas fa-calendar"></i> <b>Tgl Kembali:</b> {{ $borrowing->return_date ?? '-' }}</div>

                @if ($borrowing->status === 'approved' || $borrowing->status === 'partially_approved')
                    <div class="status-ongoing">Sedang dalam peminjaman</div>
                @elseif ($borrowing->status === 'returned' || $borrowing->status === 'partially_returned')
                    <div class="status-returned">Sudah Dikembalikan</div>
                @elseif ($borrowing->status === 'rejected')
                    <span class="status-rejected">Ditolak</span>
                @elseif ($borrowing->status === 'partial')
                    <span class="status-rejected">Disetujui Sebagian</span>
                @else
                    <span class="text-muted">Menunggu Persetujuan</span>
                @endif

                <div class="item-statuses" style="margin-top: 12px; padding: 10px; background: #f8f9fa; border-radius: 8px; font-size: 0.85rem;">
                    <div style="font-weight: 600; margin-bottom: 8px; color: #333;">Status Barang:</div>
                    @forelse ($borrowing->items as $item)
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;">
                            <span style="font-weight: 500;">{{ $item->commodity->name }}</span>
                            <span class="badge {{ $item->status === 'approved' ? 'bg-success' : ($item->status === 'rejected' ? 'bg-danger' : ($item->status === 'returned' ? 'bg-info' : 'bg-warning')) }}">
                                {{ ucfirst($item->status) }}
                            </span>
                        </div>
                    @empty
                        <span class="text-muted">Tidak ada barang.</span>
                    @endforelse
                </div>

                <div style="display: flex; gap: 10px; margin-top: auto;">
                    <a href="{{ route('students.borrowings.show', $borrowing->id) }}" class="btn-primary" style="flex: 1; text-align: center;">Detail</a>
                    @if (($borrowing->status === 'approved' || $borrowing->status === 'partially_approved') && $borrowing->items->where('status', 'approved')->count() > 0)
                        <button type="button" class="btn-primary" onclick="openReturnModal({{ $borrowing->id }})" style="flex: 1; text-align: center; width: 100%; border: none; padding: 8px 16px; background: #007bff; color: white; border-radius: 12px; cursor: pointer; font-weight: 700; font-size: 0.9rem; box-shadow: 0 4px 12px rgba(0,0,0,0.1);">Kembalikan</button>
                    @endif
                </div>
            </div>
        @empty
            <p>Tidak ada pengajuan peminjaman.</p>
        @endforelse
    </div>
</div>

<!-- Custom Modal for selecting items to return (No Bootstrap) -->
<div id="returnModal" class="custom-modal" style="display: none;">
    <div class="custom-modal-overlay" onclick="closeReturnModal()"></div>
    <div class="custom-modal-dialog">
        <div class="custom-modal-content">
            <div class="custom-modal-header">
                <h5 class="custom-modal-title">Pilih Barang untuk Dikembalikan</h5>
                <button type="button" class="custom-modal-close" onclick="closeReturnModal()">&times;</button>
            </div>
            <div class="custom-modal-body">
                <div id="itemsList">
                    <!-- Items will be populated here -->
                </div>
            </div>
            <div class="custom-modal-footer">
                <button type="button" class="custom-btn-secondary" onclick="closeReturnModal()">Batal</button>
            </div>
        </div>
    </div>
</div>

<style>
    /* Custom Modal Styles (No Bootstrap) */
    .custom-modal {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 1200;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .custom-modal-overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        backdrop-filter: blur(2px);
    }

    .custom-modal-dialog {
        position: relative;
        margin: auto;
        max-width: 500px;
        width: 90%;
        max-height: 80vh;
        overflow: hidden;
    }

    .custom-modal-content {
        background: white;
        border-radius: 8px;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
        display: flex;
        flex-direction: column;
        height: 100%;
        max-height: 80vh;
    }

    .custom-modal-header {
        padding: 16px 20px;
        border-bottom: 1px solid #dee2e6;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .custom-modal-title {
        margin: 0;
        font-size: 1.25rem;
        font-weight: 500;
        color: #333;
    }

    .custom-modal-close {
        background: none;
        border: none;
        font-size: 1.5rem;
        cursor: pointer;
        color: #6c757d;
        padding: 0;
        width: 30px;
        height: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        transition: background-color 0.2s ease;
    }

    .custom-modal-close:hover {
        background-color: #f8f9fa;
        color: #000;
    }

    .custom-modal-body {
        padding: 20px;
        flex: 1;
        overflow-y: auto;
    }

    .custom-modal-footer {
        padding: 12px 20px;
        border-top: 1px solid #dee2e6;
        display: flex;
        justify-content: flex-end;
        gap: 10px;
    }

    .custom-btn-secondary {
        background-color: #6c757d;
        color: white;
        border: none;
        padding: 8px 16px;
        border-radius: 4px;
        cursor: pointer;
        font-size: 0.875rem;
        transition: background-color 0.2s ease;
    }

    .custom-btn-secondary:hover {
        background-color: #5a6268;
    }

    .clickable-item {
        padding: 12px;
        margin-bottom: 8px;
        background-color: #f8f9fa;
        border: 1px solid #dee2e6;
        border-radius: 5px;
        cursor: pointer;
        transition: all 0.2s ease;
        font-size: 0.95rem;
    }

    .clickable-item:hover {
        background-color: #e9ecef;
        border-color: #007bff;
        box-shadow: 0 2px 4px rgba(0,123,255,0.2);
    }

    .clickable-item:last-child {
        margin-bottom: 0;
    }
</style>

<script>
    function updateSlider(cardId, newIndex) {
        const card = document.getElementById('card-' + cardId);
        const slider = document.getElementById('slider-' + cardId);
        const sliderImages = slider.querySelector('.slider-images');

        sliderImages.style.transform = `translateX(-${newIndex * 100}%)`;

        slider.dataset.index = newIndex;

        // Update dots
        const dots = document.querySelectorAll('#dots-' + cardId + ' .dot');
        dots.forEach((dot, index) => {
            dot.classList.toggle('active', index === newIndex);
        });

        // Update commodity info
        const commodities = JSON.parse(card.dataset.commodities);
        const currentCommodity = commodities[newIndex];
        const infoDiv = document.getElementById('commodity-info-' + cardId);
        infoDiv.innerHTML = `<div class="commodity-name">${currentCommodity.name} (${currentCommodity.quantity} unit)</div>`;
    }

    function nextCommodity(cardId) {
        const slider = document.getElementById('slider-' + cardId);
        const images = slider.querySelectorAll('.slider-images img, .slider-images .no-image');
        const total = images.length;
        let currentIndex = slider.dataset.index ? parseInt(slider.dataset.index) : 0;
        currentIndex = (currentIndex + 1) % total;
        updateSlider(cardId, currentIndex);
        updateButtons(cardId, currentIndex, total);
    }

    function prevCommodity(cardId) {
        const slider = document.getElementById('slider-' + cardId);
        const images = slider.querySelectorAll('.slider-images img, .slider-images .no-image');
        const total = images.length;
        let currentIndex = slider.dataset.index ? parseInt(slider.dataset.index) : 0;
        currentIndex = (currentIndex - 1 + total) % total;
        updateSlider(cardId, currentIndex);
        updateButtons(cardId, currentIndex, total);
    }

    function updateButtons(cardId, currentIndex, total) {
        const prevBtn = document.querySelector(`#card-${cardId} .slider-btn.prev`);
        const nextBtn = document.querySelector(`#card-${cardId} .slider-btn.next`);

        if (total > 1) {
            prevBtn.style.display = 'inline-block';
            nextBtn.style.display = 'inline-block';
        } else {
            prevBtn.style.display = 'none';
            nextBtn.style.display = 'none';
        }
    }

    function goToCommodity(cardId, index) {
        const slider = document.getElementById('slider-' + cardId);
        const images = slider.querySelectorAll('.slider-images img, .slider-images .no-image');
        const total = images.length;
        updateSlider(cardId, index);
        updateButtons(cardId, index, total);
    }

    function openReturnModal(borrowingId) {
        const card = document.getElementById(`card-${borrowingId}`);
        const itemsData = JSON.parse(card.dataset.items || '[]');
        if (itemsData.length === 0) {
            alert('Tidak ada barang yang dapat dikembalikan saat ini.');
            return;
        }
        const itemsList = document.getElementById('itemsList');
        itemsList.innerHTML = '';
        itemsData.forEach(item => {
            const div = document.createElement('div');
            div.className = 'clickable-item';
            div.dataset.itemId = item.id;
            div.innerHTML = `<span>${item.name}</span>`;
            div.addEventListener('click', function() {
                closeReturnModal();
                let url = '{{ route("students.borrowings.return.item", ":itemId") }}'.replace(':itemId', this.dataset.itemId);
                window.location.href = url;
            });
            itemsList.appendChild(div);
        });
        const modalElement = document.getElementById('returnModal');
        modalElement.dataset.borrowingId = borrowingId;
        modalElement.style.display = 'flex';
    }

    function closeReturnModal() {
        const modalElement = document.getElementById('returnModal');
        modalElement.style.display = 'none';
    }

    document.addEventListener('DOMContentLoaded', function() {
        @forelse ($borrowings as $borrowing)
            @if($borrowing->commodities->count() > 1)
                const slider{{ $borrowing->id }} = document.getElementById('slider-{{ $borrowing->id }}');
                slider{{ $borrowing->id }}.dataset.index = '0';
                updateButtons('{{ $borrowing->id }}', 0, {{ $borrowing->commodities->count() }});
            @endif
        @empty
        @endforelse
    });
</script>
@endsection