import decimal

from django.shortcuts import render

# Create your views here.
from rest_framework.generics import CreateAPIView, ListAPIView, UpdateAPIView, RetrieveAPIView

from equipment.models import *
from follow.views import check_if_user
from myuser.models import TemplateUser
from wallet.models import Wallet
from rest_framework.exceptions import ValidationError
from rest_framework.response import Response
from wallet.serializers import WalletCreateSerializer, WalletListSerializer
from django.db import models
from django.apps import apps


class WalletCreateAPIView(CreateAPIView):

    def post(self, request, *args, **kwargs):
        prev=Wallet.objects.filter(owner=request.user.id)
        if prev:
            raise ValidationError({"detail": "You have wallet already"})
        dict={"owner":request.user.id}
        serializer=WalletCreateSerializer(data=dict)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(serializer.data, status=200)


class WalletListAPIView(ListAPIView):
    serializer_class = WalletListSerializer
    queryset = Wallet.objects.all()


class WalletRetrieveAPIView(RetrieveAPIView):
    serializer_class = WalletListSerializer
    queryset = Wallet.objects.all()

    def get_object(self):
        request=self.request
        check_if_user(request)
        user=TemplateUser.objects.filter(id=request.user.id).first()
        if not user:
            raise ValidationError({"detail": "User does not exist"})
        return Wallet.objects.filter(owner=user).first()


class WalletDeleteAPIView(RetrieveAPIView):
    serializer_class = WalletListSerializer
    queryset = Wallet.objects.all()

    def delete(self, request, *args, **kwargs):
        check_if_user(request)
        user=TemplateUser.objects.filter(id=request.user.id).first()
        if not user:
            raise ValidationError({"detail": "User does not exist"})
        wallet=Wallet.objects.filter(owner=user).first()
        if not wallet:
            raise ValidationError({"detail": "wallet does not exist"})
        wallet.delete()
        return Response({}, status=200)


class SendUSDAPIView(UpdateAPIView):

    def put(self, request, *args, **kwargs):
        check_if_user(request)
        curr_wallet = Wallet.objects.filter(owner=request.user.id).first()
        if not curr_wallet:
            raise ValidationError({"detail": "You don't have wallet"})
        dollar=request.data.get("USD",None)
        if dollar is None:
            raise ValidationError({"detail": "Give the amount of USD"})
        is_float=isinstance(dollar, float) or isinstance(dollar, int)
        if not is_float:
            raise ValidationError({"detail": "Amount of USD must be in float or int form"})
        curr_wallet.USD=curr_wallet.USD+decimal.Decimal(dollar)
        curr_wallet.Sent_USD=curr_wallet.Sent_USD+decimal.Decimal(dollar)
        curr_wallet.save()
        update_wealth(curr_wallet)
        serializer=WalletListSerializer(curr_wallet)
        return Response(serializer.data, status=200)


class PurchaseEquipmentAPIView(UpdateAPIView):

    def put(self, request, *args, **kwargs):
        check_if_user(request)
        curr_wallet = Wallet.objects.filter(owner=request.user.id).first()
        if not curr_wallet:
            raise ValidationError({"detail": "You don't have wallet"})
        name=request.data.get('name_of_eq',None)
        amount=request.data.get('amount',None)
        if amount is None or name is None:
            raise ValidationError({"detail": "Give both amount and neme of equipment"})
        is_float=isinstance(amount, float) or isinstance(amount, int)
        if not is_float:
            raise ValidationError({"detail": "Amount must be in float or int form"})
        amount=decimal.Decimal(amount)
        is_etf=False
        is_currency=False
        if name== 'BTC' or name == 'LTC' or name== 'ETH' :
            model='CryptoCurrencies'
        elif name== 'XAG' or name == 'XAU':
            model = 'Metals'
        elif name== 'GOOGL' or name == 'AAPL' or name== 'GM' :
            model = 'Stocks'
        elif name== 'EUR' or name == 'GBP' or name== 'TRY' :
            model = 'Currencies'
            is_currency=True
        elif name== 'DJI' or name == 'IXIC' or name== 'INX' :
            model = 'TraceIndices'
        elif name== 'SPY' or name == 'IVV' or name== 'VTI' :
            model = 'ETFInformation'
            is_etf = True
        else:
            raise ValidationError({"detail": "Give a valid name"})
        equipment = apps.get_model("equipment", model)
        last=equipment.objects.all().last()
        if is_currency:
            currency = 1 / getattr(last, name)
        else:
            if is_etf:
                temp_curr=float(getattr(last, name)[1:])
                currency=round(decimal.Decimal(temp_curr),10)
            else:
                currency = getattr(last, name)
        subtract_usd = currency * amount
        curr_usd = curr_wallet.USD
        if curr_usd < subtract_usd:
            raise ValidationError({"detail": "You can not afford this amount"})
        else:
            wallet_amount = getattr(curr_wallet, name)
            curr_wallet.USD = curr_usd - subtract_usd
            setattr(curr_wallet, name, (wallet_amount+amount))
            update_wealth(curr_wallet)
            curr_wallet.save()
        serializer=WalletListSerializer(curr_wallet)
        return Response(serializer.data, status=200)


class SellEquipmentAPIView(UpdateAPIView):

    def put(self, request, *args, **kwargs):
        check_if_user(request)
        curr_wallet = Wallet.objects.filter(owner=request.user.id).first()
        if not curr_wallet:
            raise ValidationError({"detail": "You don't have wallet"})
        name=request.data.get('name_of_eq',None)
        amount=request.data.get('amount',None)
        if amount is None or name is None:
            raise ValidationError({"detail": "Give both amount and neme of equipment"})
        is_float=isinstance(amount, float) or isinstance(amount, int)
        if not is_float:
            raise ValidationError({"detail": "Amount must be in float or int form"})
        amount=round(decimal.Decimal(amount),10)
        is_currency = False
        is_etf = False
        if name== 'BTC' or name == 'LTC' or name== 'ETH' :
            model='CryptoCurrencies'
        elif name== 'XAG' or name == 'XAU':
            model = 'Metals'
        elif name== 'GOOGL' or name == 'AAPL' or name== 'GM' :
            model = 'Stocks'
        elif name== 'EUR' or name == 'GBP' or name== 'TRY' :
            is_currency = True
            model = 'Currencies'
        elif name== 'DJI' or name == 'IXIC' or name== 'INX' :
            model = 'TraceIndices'
        elif name== 'SPY' or name == 'IVV' or name== 'VTI' :
            model = 'ETFInformation'
            is_etf=True
        else:
            raise ValidationError({"detail": "Give a valid name"})
        equipment = apps.get_model("equipment", model)
        last=equipment.objects.all().last()
        if is_currency:
            currency = 1 / getattr(last, name)
        else:
            if is_etf:
                temp_curr=float(getattr(last, name)[1:])
                currency=round(decimal.Decimal(temp_curr),10)
            else:
                currency = getattr(last, name)
        wallet_amount=getattr(curr_wallet, name)
        addition_usd = currency * amount
        curr_usd = curr_wallet.USD
        if wallet_amount < amount:
            raise ValidationError({"detail": "You don't have this much equipment"})
        else:
            curr_wallet.USD = curr_usd + addition_usd
            setattr(curr_wallet, name, (wallet_amount-amount))
            update_wealth(curr_wallet)
            curr_wallet.save()
        serializer=WalletListSerializer(curr_wallet)
        return Response(serializer.data, status=200)


def update_wealth(wallet):
    list_currencies = ['BTC', 'LTC', 'ETH', 'XAG', 'XAU', 'GOOGL', 'AAPL', 'GM', 'EUR', 'GBP', 'TRY', 'DJI', 'IXIC',
                       'INX', 'SPY', 'IVV', 'VTI']
    wealth=wallet.USD
    for i in range(0,len(list_currencies)):
        is_currency=False
        is_etf=False
        if i < 3 :
            model='CryptoCurrencies'
        elif i < 5:
            model = 'Metals'
        elif i < 8 :
            model = 'Stocks'
        elif i < 11 :
            model = 'Currencies'
            is_currency=True
        elif i < 14 :
            model = 'TraceIndices'
        elif i < 17 :
            model = 'ETFInformation'
            is_etf=True
        equipment = apps.get_model("equipment", model)
        last = equipment.objects.all().last()
        if not is_currency:
            if is_etf:
                temp_curr = float(getattr(last, list_currencies[i])[1:])
                currency = round(decimal.Decimal(temp_curr), 10)
            else:
                currency = getattr(last, list_currencies[i])
        else:
            currency = 1/getattr(last, list_currencies[i])
        amount = decimal.Decimal(getattr(wallet, list_currencies[i]))
        wealth=wealth+amount*currency
    wallet.Wealth_USD=wealth
    wallet.save()







