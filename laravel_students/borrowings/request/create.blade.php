@extends('layouts.app')

@section('title', 'Buat Pengajuan Peminjaman')

@section('content')
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<style>
    /* Gaya umum dan animasi */
    body { font-family: 'Segoe UI', sans-serif; background-color: #f4f7f9; }
    @keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
    .fade-up { animation: fadeInUp 0.5s ease forwards; }

    /* Kontainer Utama */
    .container-custom { background: #fff; max-width: 1300px; margin: 30px auto; padding: 25px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
    .container-custom h1 { font-size: 24px; font-weight: 600; margin-bottom: 20px; color: #333; }

    /* Filter dan Pencarian */
    .filter-container { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 25px; align-items: center; }
    .filter-container input, .filter-container select { padding: 8px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 13px; }
    .btn-search { display: inline-flex; align-items: center; background: #007bff; color: #fff; padding: 8px 14px; border-radius: 6px; text-decoration: none; font-size: 13px; font-weight: 500; border: none; cursor: pointer; transition: background-color 0.2s; }
    .btn-search:hover { background-color: #0056b3; }

    /* Grid Kartu Produk */
    .products-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 25px; }

    /* Kartu Produk */
    .product-card { background: #fff; border: 1px solid #e9ecef; border-radius: 12px; overflow: hidden; transition: all 0.3s ease; display: flex; flex-direction: column; }
    .product-card:hover { transform: translateY(-4px); box-shadow: 0 8px 25px rgba(0,0,0,0.08); }
    .product-image { height: 180px; background: #f8f9fa; display: flex; align-items: center; justify-content: center; position: relative; }
    .product-image img { width: 100%; height: 100%; object-fit: cover; }
    .product-image .fa-box { font-size: 48px; color: #ced4da; }
    .product-stock { position: absolute; top: 12px; right: 12px; background: rgba(0,0,0,0.5); color: white; padding: 4px 10px; border-radius: 15px; font-size: 12px; font-weight: 500; }
    .product-stock.low { background: #ffc107; color: #333; }
    .product-stock.out { background: #dc3545; }

    .product-details { padding: 18px; flex-grow: 1; display: flex; flex-direction: column; }
    .product-name { font-size: 16px; font-weight: 600; color: #333; margin-bottom: 8px; line-height: 1.3; }
    .product-meta { font-size: 12px; color: #6c757d; margin-bottom: 15px; }
    .product-meta span { margin-right: 10px; }
    .product-meta i { margin-right: 4px; }

    .product-actions { margin-top: auto; display: flex; align-items: center; justify-content: space-between; background-color: #f8f9fa; padding: 12px 18px; border-top: 1px solid #e9ecef; }
    .quantity-selector { display: flex; align-items: center; gap: 8px; }
    .quantity-btn { width: 30px; height: 30px; border: 1px solid #dee2e6; background: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s; }
    .quantity-btn:hover { background: #e9ecef; border-color: #007bff; }
    .quantity-input { width: 45px; text-align: center; border: 1px solid #dee2e6; border-radius: 6px; padding: 5px; font-size: 14px; }
    .quantity-input:focus { outline: none; border-color: #007bff; }

    /* Tombol Checkout di Footer */
    .checkout-footer { position: sticky; bottom: 0; background: #fff; padding: 15px 30px; box-shadow: 0 -4px 15px rgba(0,0,0,0.08); display: flex; justify-content: space-between; align-items: center; gap: 20px; }
    .btn-checkout { background: #007bff; color: white; padding: 12px 25px; border-radius: 8px; border: none; font-size: 16px; font-weight: 600; cursor: pointer; transition: all 0.3s; }
    .btn-checkout:hover { background: #0c5db3ff; }
    .btn-checkout:disabled { background: #6c757d; cursor: not-allowed; }

    /* Modal */
    .modal { display: none; position: fixed; z-index: 1050; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.5); }
    .modal-content { background-color: #fefefe; margin: 10% auto; padding: 30px; border-radius: 12px; width: 90%; max-width: 500px; box-shadow: 0 5px 20px rgba(0,0,0,0.2); }
    .modal-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #dee2e6; padding-bottom: 15px; margin-bottom: 20px; }
    .modal-title { font-size: 20px; font-weight: 600; color: #333; }
    .close { color: #aaa; font-size: 28px; font-weight: bold; cursor: pointer; }
    .modal-body .form-group { margin-bottom: 15px; }
    .modal-body .form-label { font-weight: 500; margin-bottom: 5px; display: block; }
    .modal-body .form-control { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 6px; }
    .modal-footer { text-align: right; margin-top: 25px; }
    .btn-submit-borrow { background: #007bff; color: white; padding: 10px 20px; border-radius: 6px; border: none; font-size: 15px; cursor: pointer; }

</style>

<div class="container-custom">
    <h1>Buat Pengajuan Peminjaman (Student)</h1>

    <!-- Filter -->
    <form id="filter-form" method="GET" action="{{ route('borrowing.request.create.student') }}" class="filter-container fade-up">
        <input type="text" name="search" value="{{ request('search') }}" placeholder="Cari nama / kode...">
        <select name="jurusan">
            <option value="">Semua Jurusan</option>
            @foreach($jurusans as $jur)
                <option value="{{ $jur }}" {{ request('jurusan') == $jur ? 'selected' : '' }}>{{ $jur }}</option>
            @endforeach
        </select>
        <button type="submit" class="btn-search"><i class="fas fa-search"></i> Cari</button>
    </form>

    <!-- Form Utama -->
    <form id="borrowing-form" action="{{ route('borrowing.request.store.student') }}" method="POST">
        @csrf
        <div class="products-grid fade-up" style="animation-delay: 0.2s;">
            @forelse($commodities as $asset)
                <div class="product-card">
                    <div class="product-image">
                        @if($asset->photo)
                            <img src="{{ $asset->photo }}" alt="{{ $asset->name }}">
                        @else
                            <i class="fas fa-box"></i>
                        @endif
                        <div class="product-stock {{ $asset->stock == 0 ? 'out' : ($asset->stock <= 5 ? 'low' : '') }}">
                            Stok: {{ $asset->stock }}
                        </div>
                    </div>
                    <div class="product-details">
                        <div class="product-name">{{ $asset->name }}</div>
                                                <p class="product-meta">
                            <span><i class="fas fa-tag"></i>{{ $asset->code }}</span>
                            <span><i class="fas fa-copyright"></i>{{ $asset->merk ?? '-' }}</span>
                            <span><i class="fas fa-map-marker-alt"></i>{{ $asset->lokasi }}</span>
                            <br>
                            <span><i class="fas fa-building"></i>{{ $asset->jurusan }}</span>
                        </p>
                        <div class="product-actions">
                            <label for="quantity-{{$asset->id}}" class="sr-only">Jumlah</label>
                            <div class="quantity-selector">
                                <button type="button" class="quantity-btn" onclick="updateQuantity({{ $asset->id }}, -1, {{ $asset->stock }})">-</button>
                                <input type="number" id="quantity-{{$asset->id}}" value="{{ $cartQuantities[$asset->id] ?? 0 }}" min="0" max="{{ $asset->stock }}" class="quantity-input">
                                <button type="button" class="quantity-btn" onclick="updateQuantity({{ $asset->id }}, 1, {{ $asset->stock }})">+</button>
                            </div>
                        </div>
                    </div>
                </div>
            @empty
                <p>Tidak ada aset yang tersedia.</p>
            @endforelse

        </div>

        @if(isset($extraCommodities) && $extraCommodities->count() > 0)
            <div id="extra-commodities" style="display:none;">
                @foreach($extraCommodities as $extra)
                    <div class="product-card">
                        <div class="product-image">
                            @if($extra->photo)
                                <img src="{{ $extra->photo }}" alt="{{ $extra->name }}">
                            @else
                                <i class="fas fa-box"></i>
                            @endif
                            <div class="product-stock {{ $extra->stock == 0 ? 'out' : ($extra->stock <= 5 ? 'low' : '') }}">
                                Stok: {{ $extra->stock }}
                            </div>
                        </div>
                        <div class="product-details">
                            <div class="product-name">{{ $extra->name }}</div>
                            <p class="product-meta">
                                <span><i class="fas fa-tag"></i>{{ $extra->code }}</span>
                                <span><i class="fas fa-copyright"></i>{{ $extra->merk ?? '-' }}</span>
                                <span><i class="fas fa-map-marker-alt"></i>{{ $extra->lokasi }}</span>
                                <br>
                                <span><i class="fas fa-building"></i>{{ $extra->jurusan }}</span>
                            </p>
                            <div class="product-actions">
                                <label for="quantity-{{$extra->id}}" class="sr-only">Jumlah</label>
                                <div class="quantity-selector">
                                    <button type="button" class="quantity-btn" onclick="updateQuantity({{ $extra->id }}, -1, {{ $extra->stock }})">-</button>
                                    <input type="number" id="quantity-{{$extra->id}}" value="{{ $cartQuantities[$extra->id] ?? 0 }}" min="0" max="{{ $extra->stock }}" class="quantity-input">
                                    <button type="button" class="quantity-btn" onclick="updateQuantity({{ $extra->id }}, 1, {{ $extra->stock }})">+</button>
                                </div>
                            </div>
                        </div>
                    </div>
                @endforeach
            </div>
        @endif

        <!-- Modal Checkout -->
        <div id="checkout-modal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Konfirmasi Peminjaman</h5>
                    <span class="close" onclick="closeModal()">&times;</span>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <h5>Ringkasan Barang</h5>
                        <ul id="item-summary-list" style="list-style-type: none; padding: 0; max-height: 150px; overflow-y: auto; border: 1px solid #eee; padding: 10px; border-radius: 6px;">
                            <!-- Item yang dipilih akan muncul di sini -->
                        </ul>
                    </div>
                    <hr>
                    <div class="form-group">
                        <label for="borrow_date" class="form-label">Tanggal Peminjaman</label>
                        <input type="date" id="borrow_date" name="borrow_date" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="return_date" class="form-label">Tanggal Pengembalian</label>
                        <input type="date" id="return_date" name="return_date" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="tujuan" class="form-label">Tujuan Peminjaman</label>
                        <textarea id="tujuan" name="tujuan" class="form-control" rows="3" placeholder="Contoh: untuk praktik fotografi di kelas" required></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn-submit-borrow">Ajukan Sekarang</button>
                </div>
            </div>
        </div>
    </form>
</div>

<!-- Footer Tombol Checkout -->
<div class="checkout-footer">
    <div id="footer-summary" style="font-size: 14px; font-weight: 500; color: #333;"></div>
    <button id="btn-show-checkout" class="btn-checkout" onclick="openModal()" disabled>Buat Pengajuan</button>
</div>

<script>
    // Fungsi utama untuk mengupdate kuantitas dari tombol +/- di kartu
    async function updateQuantity(id, change, maxStock) {
        const input = document.getElementById(`quantity-${id}`);
        let newValue = parseInt(input.value) + change;
        if (newValue < 0) newValue = 0;
        if (newValue > maxStock) newValue = maxStock;

        // Update input value immediately for better UX
        input.value = newValue;

        try {
            const response = await fetch('/cart/update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content')
                },
                body: JSON.stringify({
                    commodity_id: id,
                    quantity: newValue
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response.json();

            if (result.success) {
                // Update semua tampilan
                updateAllSummaries();
            } else {
                // Revert the change if update failed
                input.value = parseInt(input.value) - change;
                Swal.fire({
                    icon: 'error',
                    title: 'Error!',
                    text: result.message || 'Gagal mengupdate keranjang',
                    showConfirmButton: false,
                    timer: 3000
                });
            }
        } catch (error) {
            // Revert the change if request failed
            input.value = parseInt(input.value) - change;
            console.error('Error updating cart:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error!',
                text: 'Terjadi kesalahan saat mengupdate keranjang. Silakan coba lagi.',
                showConfirmButton: false,
                timer: 3000
            });
        }
    }

    // Fungsi untuk mengupdate kuantitas dari tombol +/- di modal
    async function adjustModalQuantity(id, change) {
        const input = document.getElementById(`quantity-${id}`);
        const maxStock = parseInt(input.max);
        let newValue = parseInt(input.value) + change;

        if (newValue < 0) newValue = 0;
        if (newValue > maxStock) newValue = maxStock;

        // Update input asli di halaman utama
        input.value = newValue;

        // Update tampilan di modal
        const modalQuantitySpan = document.getElementById(`modal-quantity-${id}`);
        if (modalQuantitySpan) {
            if (newValue > 0) {
                modalQuantitySpan.textContent = newValue;
            } else {
                // Hapus item dari daftar modal jika kuantitas menjadi 0
                modalQuantitySpan.closest('li').remove();
            }
        }

        // Cek jika modal menjadi kosong
        if (document.getElementById('item-summary-list').children.length === 0) {
            closeModal();
        }

        // Update semua tampilan lainnya
        updateAllSummaries();

        // Update cart in backend
        try {
            await fetch('/cart/update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content')
                },
                body: JSON.stringify({
                    commodity_id: id,
                    quantity: newValue
                })
            });
        } catch (error) {
            console.error('Error updating cart:', error);
        }
    }

    // Satu fungsi untuk mengupdate footer dan status tombol checkout
    function updateAllSummaries() {
        const inputs = document.querySelectorAll('.quantity-input');
        let totalItems = 0;
        let totalTypes = 0;

        inputs.forEach(input => {
            const quantity = parseInt(input.value);
            if (quantity > 0) {
                totalTypes++;
                totalItems += quantity;
            }
        });

        // Update footer summary
        const footerSummary = document.getElementById('footer-summary');
        footerSummary.innerHTML = `<i class="fas fa-shopping-basket" style="margin-right: 10px; color: #007bff;"></i> <span><b>${totalTypes}</b> Jenis Barang</span> | <span>Total <b>${totalItems}</b> Unit</span>`;

        // Update status tombol checkout utama
        document.getElementById('btn-show-checkout').disabled = totalTypes === 0;
    }

    // Fungsi untuk membuka modal dan membangun ringkasan
    function openModal() {
        const summaryList = document.getElementById('item-summary-list');
        summaryList.innerHTML = ''; // Kosongkan daftar sebelumnya

        const inputs = document.querySelectorAll('.quantity-input');
        let hasItems = false;

        inputs.forEach(input => {
            const quantity = parseInt(input.value);
            const id = input.id.match(/\d+/)[0];

            if (quantity > 0) {
                hasItems = true;
                const card = input.closest('.product-card');
                const name = card.querySelector('.product-name').textContent;
                const photoNode = card.querySelector('.product-image img');
                const photoSrc = photoNode ? photoNode.src : 'https://via.placeholder.com/40'; // Placeholder jika tidak ada gambar

                const listItem = document.createElement('li');
                listItem.style.display = 'flex';
                listItem.style.alignItems = 'center';
                listItem.style.justifyContent = 'space-between';
                listItem.style.padding = '8px 0';
                listItem.style.borderBottom = '1px solid #f0f0f0';

                listItem.innerHTML = `
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <img src="${photoSrc}" alt="${name}" width="40" height="40" style="border-radius: 4px; object-fit: cover;">
                        <span>${name}</span>
                    </div>
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <button type="button" class="quantity-btn" onclick="adjustModalQuantity(${id}, -1)">-</button>
                        <strong id="modal-quantity-${id}">${quantity}</strong>
                        <button type="button" class="quantity-btn" onclick="adjustModalQuantity(${id}, 1)">+</button>
                    </div>
                `;
                summaryList.appendChild(listItem);
            }
        });

        if (hasItems) {
            document.getElementById('checkout-modal').style.display = 'block';
        } else {
            Swal.fire({ icon: 'error', title: 'Oops...', text: 'Pilih setidaknya satu barang untuk dipinjam.' });
        }
    }

    function closeModal() {
        document.getElementById('checkout-modal').style.display = 'none';
    }

    window.onclick = function(event) {
        if (event.target == document.getElementById('checkout-modal')) {
            closeModal();
        }
    }

    // Panggil sekali saat halaman dimuat untuk inisialisasi
    document.addEventListener('DOMContentLoaded', function() {
        updateAllSummaries();
    });

    // Tambahkan event listener ke semua input kuantitas
    document.querySelectorAll('.quantity-input').forEach(input => {
        input.addEventListener('change', function() {
            const id = this.id.match(/\d+/)[0];
            const newValue = parseInt(this.value);
            const maxStock = parseInt(this.max);

            if (newValue < 0) this.value = 0;
            if (newValue > maxStock) this.value = maxStock;

            // Update cart in backend
            fetch('/cart/update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content')
                },
                body: JSON.stringify({
                    commodity_id: id,
                    quantity: parseInt(this.value)
                })
            }).catch(error => console.error('Error updating cart:', error));

            updateAllSummaries();
        });
    });

    // Validasi tanggal
    const today = new Date();
    const todayString = today.toISOString().split('T')[0];
    document.getElementById('borrow_date').setAttribute('min', todayString);

    document.getElementById('borrow_date').addEventListener('change', function() {
        let borrowDate = new Date(this.value);
        borrowDate.setDate(borrowDate.getDate() + 1);
        let minReturnDate = borrowDate.toISOString().split('T')[0];
        document.getElementById('return_date').setAttribute('min', minReturnDate);
    });

    // Debug form submission
    document.getElementById('borrowing-form').addEventListener('submit', function(e) {
        console.log('Form submitted!');
        console.log('Form data:', new FormData(this));
        console.log('Borrow date:', document.getElementById('borrow_date').value);
        console.log('Return date:', document.getElementById('return_date').value);
        console.log('Tujuan:', document.getElementById('tujuan').value);
    });

    // Alert dari session
    @if(session('success'))
        Swal.fire({ icon: 'success', title: 'Berhasil!', text: '{{ session('success') }}', showConfirmButton: false, timer: 2500 });
    @endif
    @if(session('error'))
        Swal.fire({ icon: 'error', title: 'Gagal!', text: '{{ session('error') }}', showConfirmButton: false, timer: 3500 });
    @endif
</script>
@endsection
