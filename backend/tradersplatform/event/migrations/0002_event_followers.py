# Generated by Django 2.2.6 on 2019-10-14 14:46

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('myuser', '0001_initial'),
        ('event', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='event',
            name='followers',
            field=models.ManyToManyField(blank=True, to='myuser.TemplateUser'),
        ),
    ]
