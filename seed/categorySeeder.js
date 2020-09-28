
const Category = require('../models/category');

var categories = [
    new Category({
        category_imagePath: 'https://salt.tikicdn.com/cache/w60/ts/category/88/a3/23/4025f0ccef53189c957bd6c2fc79cb58.png',
        category_name: 'Sách'
    }),
    new Category({
        category_imagePath: 'https://salt.tikicdn.com/cache/w60/ts/category/dd/51/92/e6bc22b5ec0d6d965a93f056b7776493.png',
        category_name: 'Thời trang-phụ kiện'
    }),
    new Category({
        category_imagePath: 'https://salt.tikicdn.com/cache/w60/ts/category/12/29/a2/7409ff03cff5c0d3d695cb19f8f15896.png',
        category_name: 'Nhà cửa-đời sống'
    }),
    new Category({
        category_imagePath: 'https://salt.tikicdn.com/cache/w60/ts/category/b3/2b/72/8e7b4b703653050ffc79efc8ee017bd0.png',
        category_name: 'Điện tử-điện lạnh'
    }),
    new Category({
        category_imagePath: 'https://salt.tikicdn.com/cache/w60/ts/category/93/27/e3/192b0ebe1d4658c51f9931bda62489b2.png',
        category_name: 'Điện Thoại-Máy Tính Bảng'
    }),
];
categories.forEach(item=>{
    item.save((err, Category)=>{
        if(err) return console.error(err);
        console.log(Category.category_name, 'save to categories collection.');
        }
    )
})
