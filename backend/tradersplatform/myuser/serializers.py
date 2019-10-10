from rest_framework.serializers import ModelSerializer

from auth.serializers import UserListSerializer
from .models import TemplateUser
from rest_framework.exceptions import ValidationError
from django.contrib.auth.models import User
from django.contrib.auth.models import Group
from django.contrib.auth import authenticate
from rest_framework.fields import CharField
from rest_framework_jwt.settings import api_settings

jwt_payload_handler = api_settings.JWT_PAYLOAD_HANDLER
jwt_encode_handler = api_settings.JWT_ENCODE_HANDLER


class TempUserCreateSerializer(ModelSerializer):

    class Meta:
        model = TemplateUser
        fields = [
            'id',
            'username',
            'email',
            'first_name',
            'last_name',
            'password',
            'location',
            'phone_number',
            'iban_number',
            'citizenship_number',
        ]
        extra_kwargs = {"password": {"write_only": True,"required": False},
                        "phone_number": {"required": False},
                        "location": {"required": False},
                        "iban_number":{"required":False},
                        "citizenship_number":{"required":False},
                        }


class TempUserLoginSerializer(ModelSerializer):
    token = CharField(allow_blank=True, read_only=True)
    username = CharField(write_only=True, required=True)
    user = UserListSerializer(read_only=True)

    class Meta:
        model = User
        fields = [
            'username',
            'password',
            'token',
            'user',

        ]
        extra_kwargs = {"password":
                            {"write_only": True}
                        }

    def validate(self, data):
        password = data.get("password")
        username = data.get("username")
        user = TemplateUser.objects.filter(username=username).distinct()
        user2 = authenticate(username=username, password=password)
        if user.exists() and user.count() == 1:
            user_obj = user.first()
        else:
            raise ValidationError("Incorrect credential")
        if user_obj:
            if not user_obj.check_password(password):
                raise ValidationError("Incorrect credential")

        payload = jwt_payload_handler(user_obj)
        token = jwt_encode_handler(payload)
        data["token"] = token
        data["user"] = user_obj
        return data