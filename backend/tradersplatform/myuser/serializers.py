import django.utils.timezone
from rest_framework.serializers import ModelSerializer

from auth.serializers import UserListSerializer
from .models import TemplateUser
from rest_framework.exceptions import ValidationError
from django.contrib.auth.models import User
from django.contrib.auth.models import Group
from django.contrib.auth import authenticate
from rest_framework.fields import CharField, SerializerMethodField
from rest_framework_jwt.settings import api_settings

jwt_payload_handler = api_settings.JWT_PAYLOAD_HANDLER
jwt_encode_handler = api_settings.JWT_ENCODE_HANDLER


class TempUserCreateSerializer(ModelSerializer):
    groups = SerializerMethodField()

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
            'biography',
            'title',
            'last_changed_password_date',
            'photo',
            'is_public',
            'groups'
        ]
        extra_kwargs = {"password": {"write_only": True, "required": False},
                        "phone_number": {"required": False},
                        "location": {"required": False},
                        "iban_number": {"required": False},
                        "citizenship_number": {"required": False},
                        "title": {"required": False},
                        "biography": {"required": False},
                        "last_changed_password_date": {"required": False},
                        "photo": {"required": False}
                        }

    def get_groups(self, obj):
        groups = obj.groups.all()
        g = [group.name for group in groups]
        return g


class TempUserLoginSerializer(ModelSerializer):
    token = CharField(allow_blank=True, read_only=True)
    username = CharField(write_only=True, required=True)
    user = UserListSerializer(read_only=True)

    class Meta:
        model = User
        fields = [
            'id',
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
            raise ValidationError({"detail": "Incorrect credential"})

        if user_obj:
            if not user_obj.check_password(password):
                raise ValidationError({"detail": "Incorrect credential"})
            if (getattr(user_obj, "last_changed_password_date") + django.utils.timezone.timedelta(6 * 365 / 12)) \
                    < django.utils.timezone.now():
                raise ValidationError({"detail": "Password out of date"})

        payload = jwt_payload_handler(user_obj)
        token = jwt_encode_handler(payload)
        data["token"] = token
        data["user"] = user_obj
        return data


class UserUpdateSerializer(ModelSerializer):
    class Meta:
        model = TemplateUser
        fields = [
            'id',
            'first_name',
            'last_name',
            'location',
            'phone_number',
            'iban_number',
            'biography',
            'title',
            'photo',
            'is_public',
        ]

        extra_kwargs = {"first_name":
                            {"required": False},
                        "last_name":
                            {"required": False},
                        "title":
                            {"required": False},
                        "biography":
                            {"required": False},
                        "is_public":
                            {"required": False},
                        "phone_number": {"required": False},
                        "location": {"required": False},
                        "iban_number": {"required": False},
                        "photo": {"required": False}
                        }


class UserDetailSerializer(ModelSerializer):
    class Meta:
        model = TemplateUser
        fields =  '__all__'


class TempUserListSerializer(ModelSerializer):
    class Meta:
        model = TemplateUser
        fields = [
            'id',
            'username',
            'email',
            'first_name',
            'last_name'
        ]
