# Generated by Django 2.2.6 on 2019-12-04 11:29

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('myuser', '0007_auto_20191124_1437'),
    ]

    operations = [
        migrations.CreateModel(
            name='Portfolio',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(blank=True, default='default', max_length=300, null=True)),
                ('BTC', models.BooleanField(default=False)),
                ('ETH', models.BooleanField(default=False)),
                ('LTC', models.BooleanField(default=False)),
                ('XAG', models.BooleanField(default=False)),
                ('XAU', models.BooleanField(default=False)),
                ('GOOGL', models.BooleanField(default=False)),
                ('AAPL', models.BooleanField(default=False)),
                ('GM', models.BooleanField(default=False)),
                ('EUR', models.BooleanField(default=False)),
                ('GBP', models.BooleanField(default=False)),
                ('TRY', models.BooleanField(default=False)),
                ('SPY', models.BooleanField(default=False)),
                ('IVV', models.BooleanField(default=False)),
                ('VTI', models.BooleanField(default=False)),
                ('DJI', models.BooleanField(default=False)),
                ('IXIC', models.BooleanField(default=False)),
                ('INX', models.BooleanField(default=False)),
                ('owner', models.ForeignKey(default='', on_delete=django.db.models.deletion.CASCADE, to='myuser.TemplateUser')),
            ],
        ),
    ]
