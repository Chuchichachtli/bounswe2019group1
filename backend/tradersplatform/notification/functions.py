import decimal

from notification.models import SetNotification, Notification, BuyOrder, SellOrder
from django.apps import apps
from rest_framework.exceptions import ValidationError
from datetime import datetime

from prediction.models import Prediction
from wallet.models import Wallet
from wallet.views import update_wealth
import os


def set_notification():
    query=SetNotification.objects.all()
    for set_notification in query:
        currency=set_notification.currency
        amount=set_notification.amount
        equipment=get_equipment(currency)
        last = equipment.objects.all().last()
        currency_value=getattr(last, currency)
        if set_notification.is_bigger:
            if currency_value>amount:
                print("create notification bigger")
                new_notification=Notification(
                    owner=set_notification.owner,
                    date=datetime.now(),
                    text=currency+" has risen wanted level"
                )
                new_notification.save()
                set_notification.delete()
        else:
            if currency_value<amount:
                new_notification = Notification(
                    owner=set_notification.owner,
                    date=datetime.now(),
                    text=currency + " has fallen wanted level"
                )
                new_notification.save()
                set_notification.delete()


def buy_order():
    query = BuyOrder.objects.all()
    for buy_order in query:
        currency = buy_order.currency
        amount = buy_order.amount
        buy_amount=buy_order.buy_amount
        equipment = get_equipment(currency)
        last = equipment.objects.all().last()
        currency_value = getattr(last, currency)
        curr_wallet = Wallet.objects.filter(owner=buy_order.owner).first()
        if not curr_wallet:
            raise ValidationError({"detail": "You don't have wallet"})
        if amount>currency_value:
            subtract_usd = currency_value * buy_amount
            curr_usd = curr_wallet.USD
            if curr_usd < subtract_usd:
                owner=buy_order.owner
                notif = Notification(
                    owner=owner,
                    text="You can not afford this amount",
                    date=datetime.now()
                )
                notif.save()
                buy_order.delete()
            else:
                wallet_amount = getattr(curr_wallet, currency)
                curr_wallet.USD = curr_usd - subtract_usd
                setattr(curr_wallet, currency, (wallet_amount + buy_amount))
                update_wealth(curr_wallet)
                curr_wallet.save()
                owner = buy_order.owner
                notif = Notification(
                    owner=owner,
                    text="Your buy order about "+currency+" has successfully done",
                    date=datetime.now()
                )
                notif.save()
                buy_order.delete()


def sell_order():
    query = SellOrder.objects.all()
    for sell_order in query:
        currency = sell_order.currency
        amount = sell_order.amount
        sell_amount=sell_order.sell_amount
        equipment = get_equipment(currency)
        last = equipment.objects.all().last()
        currency_value = getattr(last, currency)
        curr_wallet = Wallet.objects.filter(owner=sell_order.owner).first()
        if not curr_wallet:
            raise ValidationError({"detail": "You don't have wallet"})
        if amount<currency_value:
            wallet_amount = getattr(curr_wallet, currency)
            addition_usd = currency_value * sell_amount
            curr_usd = curr_wallet.USD
            if wallet_amount < sell_amount:
                notif = Notification(
                    owner=sell_order.owner,
                    text="You don't have this much equipment",
                    date=datetime.now()
                )
                notif.save()
                sell_order.delete()
            else:
                curr_wallet.USD = curr_usd + addition_usd
                setattr(curr_wallet, currency, (wallet_amount - sell_amount))
                update_wealth(curr_wallet)
                curr_wallet.save()
                notif = Notification(
                    owner=sell_order.owner,
                    text="Your sell order about "+currency+" has successfully done",
                    date=datetime.now()
                )
                notif.save()
                sell_order.delete()


def predict():
    query = Prediction.objects.all()
    for prediction in query:
        user = prediction.user
        user.prediction_counter += 1
        currency = prediction.tradingEquipment
        equipment = get_equipment(currency)
        last = equipment.objects.all().last()
        one_before_last = equipment.objects.all().reverse()[0]
        last_value = getattr(last, currency)
        one_before_last_value = getattr(one_before_last, currency)
        if prediction.is_Rising:
            if last_value > one_before_last_value:
                user.correct_prediction_counter+=1
        else:
            if last_value < one_before_last_value:
                user.correct_prediction_counter += 1
        user.save()
        prediction.delete()



def get_equipment(value):
    list_currencies = ['BTC', 'LTC', 'ETH', 'XAG', 'XAU', 'GOOGL', 'AAPL', 'GM', 'EUR', 'GBP', 'TRY', 'DJI', 'IXIC',
                       'INX', 'SPY', 'IVV', 'VTI']
    dict = {}
    for i in range(0, len(list_currencies)):
        if value == list_currencies[i]:
            if i < 3:
                model = 'CryptoCurrencies'
            elif i < 5:
                model = 'Metals'
            elif i < 8:
                model = 'Stocks'
            elif i < 11:
                model = 'Currencies'
            elif i < 14:
                model = 'TraceIndices'
            elif i < 17:
                model = 'ETFInformation'
            equipment = apps.get_model("equipment", model)
            return equipment
    raise ValidationError({"detail": "That currency does not exist"})
