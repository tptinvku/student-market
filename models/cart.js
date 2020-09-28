
module.exports = function Cart(oldCart) {
    this.items = oldCart.items || {};
    this.totalQty = oldCart.totalQty || 0;
    this.totalPrice = oldCart.totalPrice || 0;
    
    this.add = function(item, id, quanities) {
        var storedItem = this.items[id];
        if (!storedItem) {
            storedItem = this.items[id] = {
                item:item,
                qty: 0,
                price: 0
            };
        }
        storedItem.qty = quanities;
        storedItem.price = storedItem.item.productPrice * quanities;
        this.totalQty++;
        this.totalPrice += storedItem.price;
    };
    
    this.generateArray = function() {
        var arr = [];
        for (let id in this.items) {
            arr.push(this.items[id]);
        }
        return arr;
    };
};