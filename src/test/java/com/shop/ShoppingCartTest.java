package com.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {

    private ShoppingCart cartIL;
    private ShoppingCart cartOther;
    private ShoppingCart cartNextDay;

    @BeforeEach
    void setUp() {
        cartIL = new ShoppingCart(State.IL, ShippingType.STANDARD);
        cartOther = new ShoppingCart(State.OTHER, ShippingType.STANDARD);
        cartNextDay = new ShoppingCart(State.OTHER, ShippingType.NEXT_DAY);
    }

    @Test
    void item_setQuantity_updatesCorrectly() {
        Item item = new Item("Apple", 2.00, 1);
        item.setQuantity(5);
        assertEquals(5, item.getQuantity());
    }

    @Test
    void cart_getState_returnsCorrectState() {
        assertEquals(State.IL, cartIL.getState());
    }

    @Test
    void cart_getShippingType_returnsCorrectType() {
        assertEquals(ShippingType.STANDARD, cartIL.getShippingType());
    }

    // ─── Add Item ───────────────────────────────────────────

    @Test
    void addItem_returnsCorrectCartSize() {
        cartIL.addItem(new Item("Apple", 2.00, 2));
        int size = cartIL.addItem(new Item("Bread", 3.00, 1));
        assertEquals(2, size);
    }

    @Test
    void addItem_quantityZero_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                cartIL.addItem(new Item("Apple", 2.00, 0))
        );
    }

    @Test
    void addItem_quantityNegative_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                cartIL.addItem(new Item("Apple", 2.00, -3))
        );
    }

    // ─── Remove Item ─────────────────────────────────────────

    @Test
    void removeItem_existingItem_removesSuccessfully() {
        cartIL.addItem(new Item("Apple", 2.00, 1));
        cartIL.removeItem("Apple");
        assertTrue(cartIL.getItems().isEmpty());
    }

    @Test
    void removeItem_caseInsensitive_removesSuccessfully() {
        cartIL.addItem(new Item("Apple", 2.00, 1));
        cartIL.removeItem("apple");
        assertTrue(cartIL.getItems().isEmpty());
    }

    @Test
    void removeItem_notFound_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                cartIL.removeItem("Ghost Item")
        );
    }

    // ─── Edit Quantity ────────────────────────────────────────

    @Test
    void editQuantity_validQuantity_updatesCorrectly() {
        cartIL.addItem(new Item("Apple", 2.00, 1));
        cartIL.editQuantity("Apple", 5);
        assertEquals(5, cartIL.getItems().get(0).getQuantity());
    }

    @Test
    void editQuantity_zero_throwsException() {
        cartIL.addItem(new Item("Apple", 2.00, 1));
        assertThrows(IllegalArgumentException.class, () ->
                cartIL.editQuantity("Apple", 0)
        );
    }

    @Test
    void editQuantity_itemNotFound_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                cartIL.editQuantity("Ghost", 3)
        );
    }

    // ─── Tax ─────────────────────────────────────────────────

    @Test
    void getTax_illinoisState_chargesSixPercent() {
        cartIL.addItem(new Item("Item", 100.00, 1));
        assertEquals(6.00, cartIL.getTax(), 0.001);
    }

    @Test
    void getTax_californiaState_chargesSixPercent() {
        ShoppingCart cartCA = new ShoppingCart(State.CA, ShippingType.STANDARD);
        cartCA.addItem(new Item("Item", 100.00, 1));
        assertEquals(6.00, cartCA.getTax(), 0.001);
    }

    @Test
    void getTax_newYorkState_chargesSixPercent() {
        ShoppingCart cartNY = new ShoppingCart(State.NY, ShippingType.STANDARD);
        cartNY.addItem(new Item("Item", 100.00, 1));
        assertEquals(6.00, cartNY.getTax(), 0.001);
    }

    @Test
    void getTax_otherState_noTax() {
        cartOther.addItem(new Item("Item", 100.00, 1));
        assertEquals(0.00, cartOther.getTax(), 0.001);
    }

    // ─── Shipping ─────────────────────────────────────────────

    @Test
    void getShipping_standard_under50_chargesTen() {
        cartOther.addItem(new Item("Item", 20.00, 1));
        assertEquals(10.00, cartOther.getShippingCost(), 0.001);
    }

    @Test
    void getShipping_standard_over50_isFree() {
        cartOther.addItem(new Item("Item", 60.00, 1));
        assertEquals(0.00, cartOther.getShippingCost(), 0.001);
    }

    @Test
    void getShipping_standard_exactly50_chargesTen() {
        cartOther.addItem(new Item("Item", 50.00, 1));
        assertEquals(10.00, cartOther.getShippingCost(), 0.001);
    }

    @Test
    void getShipping_nextDay_alwaysChargesTwentyFive() {
        cartNextDay.addItem(new Item("Item", 100.00, 1));
        assertEquals(25.00, cartNextDay.getShippingCost(), 0.001);
    }

    @Test
    void getShipping_nextDay_under50_stillChargesTwentyFive() {
        cartNextDay.addItem(new Item("Item", 10.00, 1));
        assertEquals(25.00, cartNextDay.getShippingCost(), 0.001);
    }

    // ─── Total ────────────────────────────────────────────────

    @Test
    void getTotal_correctSumOfRawTaxShipping() {
        cartIL.addItem(new Item("Item", 100.00, 1));
        // raw=100, tax=6, shipping=0 (over 50)
        assertEquals(106.00, cartIL.getTotal(), 0.001);
    }

    @Test
    void getTotal_emptyCart_shippingAloneDoesNotTriggerMinimum() {
        ShoppingCart cart = new ShoppingCart(State.OTHER, ShippingType.STANDARD);
        cart.addItem(new Item("Item", 0.01, 1));
        // 0.01 raw + 10.00 shipping = 10.01, above minimum, no exception
        assertDoesNotThrow(cart::getTotal);
    }

    @Test
    void getTotal_exceedsMaximum_throwsException() {
        ShoppingCart cart = new ShoppingCart(State.OTHER, ShippingType.STANDARD);
        cart.addItem(new Item("Item", 99999.99, 2));
        assertThrows(IllegalStateException.class, cart::getTotal);
    }

    // ─── Checkout ─────────────────────────────────────────────

    @Test
    void checkout_validCart_returnsTransactionCompleted() {
        cartIL.addItem(new Item("Item", 10.00, 1));
        assertEquals("transaction completed", cartIL.checkout());
    }

    @Test
    void checkout_clearsCartAfterSuccess() {
        cartIL.addItem(new Item("Item", 10.00, 1));
        cartIL.checkout();
        assertTrue(cartIL.getItems().isEmpty());
    }

    @Test
    void checkout_emptyCart_throwsException() {
        assertThrows(IllegalStateException.class, () ->
                cartIL.checkout()
        );
    }

    // ─── Item class ───────────────────────────────────────────

    @Test
    void item_getSubtotal_correctCalculation() {
        Item item = new Item("Apple", 2.00, 3);
        assertEquals(6.00, item.getSubtotal(), 0.001);
    }

    @Test
    void item_toString_correctFormat() {
        Item item = new Item("Apple", 2.00, 3);
        assertEquals("Apple - $2.00 x 3 = $6.00", item.toString());
    }
}