const Product = require('../models/product');

let products = [
    new Product({
        categoryId: '5ee70fb0ebbc009d775bc897',
        product_imagePath: 'https://salt.tikicdn.com/cache/280x280/ts/product/3b/3a/31/3e59cf4b36e07b40cae875e3e7be9142.jpg',
        product_name: 'iPad 10.2 Inch WiFi 32GB New 2019 - Hàng Chính Hãng',
        product_description: 'empty!',
        product_price: 8990000
    }),
    new Product({
        categoryId: '5ee70fb0ebbc009d775bc894',
        product_imagePath: 'https://salt.tikicdn.com/cache/280x392/ts/product/40/5b/da/8fb3dfe89367fcd20ad82223df811d2d.jpg',
        product_name: 'Áo thun nam',
        product_description: 'empty!',
        product_price: 44000
    }),
    new Product({
        categoryId: '5ee70fb0ebbc009d775bc895',
        product_imagePath: 'https://salt.tikicdn.com/cache/280x280/ts/product/15/be/3f/5eb8c4818ec6f74c96441cbd32f8c1bd.jpg',
        product_name: 'Cây Lau Nhà Dạng Đứng Lock&Lock ETM461 (26 x 12.8 cm) - Trắng Phối Màu',
        product_description: 'empty!',
        product_price: 267000
    }),
    new Product({
        categoryId: '5ee70fb0ebbc009d775bc893',
        product_imagePath: 'https://salt.tikicdn.com/cache/280x280/ts/product/b2/56/d3/17262447faaef713be60d6b25ec0551d.jpg',
        product_name: 'Vui Vẻ Không Quạu Nha',
        product_description: 'empty!',
        product_price: 49510
    }),
    new Product({
        categoryId: '5ee70fb0ebbc009d775bc896',
        product_imagePath: 'https://salt.tikicdn.com/cache/280x280/ts/product/1b/50/42/3a50198e45f02ab076be0fc114fd3746.jpg',
        product_name: 'Máy Lạnh Casper SC-09TL22 (1.0HP) - Hàng Chính Hãng',
        product_description: 'empty!',
        product_price: 5865000
    }),
    new Product({
        categoryId: '5ee70fb0ebbc009d775bc897',
        product_imagePath: 'https://salt.tikicdn.com/cache/280x280/ts/product/ab/11/83/faeea4f22fdc230a07e517bf9fcd248d.jpg',
        product_name: 'Điện Thoại Samsung Galaxy Note 10 Lite (128GB8GB) - Hàng Chính Hãng - Đã Kích Hoạt Bảo Hành Điện Tử',
        product_description: 'empty!',
        product_price: 9590000
    }),
]

products.forEach(item=>{
        item.save((err, Category)=>{
        if(err) return console.error(err);
        console.log(Category.product_name, 'save to products collection.');
            }
        )
})